package com.aetherfocus.data.source

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.aetherfocus.core.common.PermissionUtils
import com.aetherfocus.core.model.BlockedApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDetectionDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val usageStatsManager =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
    private val packageManager: PackageManager = context.packageManager

    /**
     * Determines the currently active foreground package name.
     * Queries usage events within a narrow lookback window (default 2000ms) for high efficiency.
     */
    fun getForegroundPackageName(lookbackWindowMs: Long = 2000L): String? {
        if (!PermissionUtils.hasUsageStatsPermission(context) || usageStatsManager == null) {
            return null
        }

        val endTime = System.currentTimeMillis()
        val beginTime = endTime - lookbackWindowMs

        val usageEvents = usageStatsManager.queryEvents(beginTime, endTime) ?: return null
        val event = UsageEvents.Event()

        var latestForegroundPackage: String? = null
        var latestTimestamp = 0L

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)
            if (event.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                if (event.timeStamp >= latestTimestamp) {
                    latestTimestamp = event.timeStamp
                    latestForegroundPackage = event.packageName
                }
            }
        }

        return latestForegroundPackage
    }

    /**
     * Returns user-friendly app label from package name.
     */
    fun getAppLabel(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    /**
     * Retrieves all installed user-launchable applications without needing QUERY_ALL_PACKAGES.
     */
    fun getInstalledLauncherApps(): List<BlockedApp> {
        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(
            launcherIntent,
            PackageManager.MATCH_ALL
        )

        val ownPackage = context.packageName

        return resolveInfos
            .mapNotNull { resolveInfo ->
                val pkgName = resolveInfo.activityInfo.packageName
                if (pkgName == ownPackage) return@mapNotNull null

                val label = resolveInfo.loadLabel(packageManager).toString()
                val isDefaultDistraction = BlockedApp.DEFAULT_DISTRACTION_PACKAGES.contains(pkgName)

                BlockedApp(
                    packageName = pkgName,
                    appName = label,
                    isBlocked = isDefaultDistraction,
                    isDefaultDistraction = isDefaultDistraction
                )
            }
            .distinctBy { it.packageName }
            .sortedWith(
                compareByDescending<BlockedApp> { it.isDefaultDistraction }
                    .thenBy { it.appName.lowercase() }
            )
    }
}

