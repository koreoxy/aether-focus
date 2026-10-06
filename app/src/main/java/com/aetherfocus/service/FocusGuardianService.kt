package com.aetherfocus.service

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.aetherfocus.AetherApp
import com.aetherfocus.MainActivity
import com.aetherfocus.R
import com.aetherfocus.core.datastore.SessionPreferences
import com.aetherfocus.core.datastore.SessionPreferencesManager
import com.aetherfocus.data.source.AppDetectionDataSource
import com.aetherfocus.domain.repository.FocusSessionRepository
import com.aetherfocus.domain.usecase.CheckDistractionUseCase
import com.aetherfocus.domain.usecase.StopFocusSessionUseCase
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class FocusGuardianService : Service() {

    @Inject
    lateinit var sessionPreferencesManager: SessionPreferencesManager

    @Inject
    lateinit var appDetectionDataSource: AppDetectionDataSource

    @Inject
    lateinit var checkDistractionUseCase: CheckDistractionUseCase

    @Inject
    lateinit var stopFocusSessionUseCase: StopFocusSessionUseCase

    @Inject
    lateinit var focusSessionRepository: FocusSessionRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var pollingJob: Job? = null
    private var progressTrackingJob: Job? = null

    private var currentPreferences: SessionPreferences = SessionPreferences()
    private var isScreenOn: Boolean = true
    private var screenStateReceiver: ScreenStateReceiver? = null

    // Debounce tracking
    private var lastTriggeredPackage: String? = null
    private var lastTriggerTimestamp: Long = 0L

    override fun onCreate() {
        super.onCreate()

        // Register dynamic screen receiver for battery efficiency
        screenStateReceiver = ScreenStateReceiver { screenOn ->
            isScreenOn = screenOn
            if (screenOn) {
                resumePolling()
            } else {
                pausePolling()
            }
        }.also { receiver ->
            registerReceiver(receiver, ScreenStateReceiver.createFilter())
        }

        // Observe active session preferences
        serviceScope.launch {
            sessionPreferencesManager.preferencesFlow.collectLatest { prefs ->
                currentPreferences = prefs
                if (!prefs.isFocusActive) {
                    stopSelf()
                } else {
                    updateNotification(prefs)
                }
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP_GUARDIAN -> {
                serviceScope.launch {
                    stopFocusSessionUseCase(isCompletedNaturally = false)
                    stopSelf()
                }
                return START_NOT_STICKY
            }
            ACTION_START_GUARDIAN, null -> {
                startInForeground()
                startAdaptivePolling()
                startProgressTracker()
            }
        }
        return START_STICKY
    }

    private fun startInForeground() {
        val initialNotification = buildNotification(currentPreferences)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            startForeground(
                AetherApp.GUARDIAN_NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
            )
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                AetherApp.GUARDIAN_NOTIFICATION_ID,
                initialNotification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_NONE
            )
        } else {
            startForeground(AetherApp.GUARDIAN_NOTIFICATION_ID, initialNotification)
        }
    }

    private fun startAdaptivePolling() {
        pollingJob?.cancel()
        pollingJob = serviceScope.launch {
            while (isActive) {
                if (isScreenOn && currentPreferences.isFocusActive) {
                    val foregroundPackage = appDetectionDataSource.getForegroundPackageName()

                    if (foregroundPackage != null && checkDistractionUseCase(foregroundPackage, currentPreferences)) {
                        handleDistractionDetected(foregroundPackage)
                    } else if (foregroundPackage != null && foregroundPackage != lastTriggeredPackage) {
                        // Reset debounce if user navigates away from the distraction
                        lastTriggeredPackage = null
                    }
                }

                delay(POLL_INTERVAL_MS)
            }
        }
    }

    private fun handleDistractionDetected(packageName: String) {
        val now = System.currentTimeMillis()
        val isSamePackage = packageName == lastTriggeredPackage
        val hasDebounceExpired = (now - lastTriggerTimestamp) > INTERVENTION_DEBOUNCE_MS

        if (!isSamePackage || hasDebounceExpired) {
            lastTriggeredPackage = packageName
            lastTriggerTimestamp = now

            val appLabel = appDetectionDataSource.getAppLabel(packageName)
            InterventionLauncher.launchIntervention(
                context = applicationContext,
                packageName = packageName,
                appName = appLabel
            )
        }
    }

    private fun startProgressTracker() {
        progressTrackingJob?.cancel()
        progressTrackingJob = serviceScope.launch {
            while (isActive) {
                delay(5000L) // Update progress and notification every 5 seconds
                if (currentPreferences.isFocusActive && currentPreferences.activeSessionId > 0) {
                    val elapsedSeconds = if (currentPreferences.sessionStartTime > 0) {
                        ((System.currentTimeMillis() - currentPreferences.sessionStartTime) / 1000).toInt()
                    } else 0

                    val targetSeconds = currentPreferences.sessionTargetDurationMinutes * 60

                    // Check if time is completed
                    if (elapsedSeconds >= targetSeconds) {
                        stopFocusSessionUseCase(isCompletedNaturally = true)
                        stopSelf()
                        break
                    } else {
                        focusSessionRepository.updateSessionProgress(
                            currentPreferences.activeSessionId,
                            elapsedSeconds
                        )
                        updateNotification(currentPreferences)
                    }
                }
            }
        }
    }

    private fun pausePolling() {
        pollingJob?.cancel()
        pollingJob = null
    }

    private fun resumePolling() {
        if (pollingJob == null || pollingJob?.isActive == false) {
            startAdaptivePolling()
        }
    }

    private fun updateNotification(prefs: SessionPreferences) {
        val notification = buildNotification(prefs)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        notificationManager.notify(AetherApp.GUARDIAN_NOTIFICATION_ID, notification)
    }

    private fun buildNotification(prefs: SessionPreferences): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        val openAppPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val stopIntent = Intent(this, FocusGuardianService::class.java).apply {
            action = ACTION_STOP_GUARDIAN
        }
        val stopPendingIntent = PendingIntent.getService(
            this,
            1,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val elapsedMinutes = if (prefs.sessionStartTime > 0) {
            ((System.currentTimeMillis() - prefs.sessionStartTime) / (1000 * 60)).toInt()
        } else 0
        val remainingMinutes = (prefs.sessionTargetDurationMinutes - elapsedMinutes).coerceAtLeast(0)

        val title = if (prefs.activeGoalTitle.isNotEmpty()) {
            "Focusing: ${prefs.activeGoalTitle}"
        } else {
            getString(R.string.guardian_notification_title)
        }

        val contentText = "$remainingMinutes min remaining • Focus Guardian is Active"

        return NotificationCompat.Builder(this, AetherApp.GUARDIAN_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(contentText)
            .setContentIntent(openAppPendingIntent)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .addAction(
                android.R.drawable.ic_menu_close_clear_cancel,
                "Stop Session",
                stopPendingIntent
            )
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        screenStateReceiver?.let {
            unregisterReceiver(it)
        }
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val ACTION_START_GUARDIAN = "com.aetherfocus.action.START_GUARDIAN"
        const val ACTION_STOP_GUARDIAN = "com.aetherfocus.action.STOP_GUARDIAN"

        private const val POLL_INTERVAL_MS = 800L
        private const val INTERVENTION_DEBOUNCE_MS = 6000L
    }
}
