package com.aetherfocus.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.aetherfocus.core.datastore.SessionPreferencesManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

    @Inject
    lateinit var sessionPreferencesManager: SessionPreferencesManager

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.IO).launch {
                val prefs = sessionPreferencesManager.preferencesFlow.first()
                if (prefs.isFocusActive) {
                    val elapsedMillis = System.currentTimeMillis() - prefs.sessionStartTime
                    val totalTargetMillis = prefs.sessionTargetDurationMinutes * 60 * 1000L

                    if (elapsedMillis < totalTargetMillis) {
                        // Session still has remaining time: resume FocusGuardianService
                        ServiceController.startGuardian(context)
                    } else {
                        // Session expired during power off: clear active state
                        sessionPreferencesManager.stopSession()
                    }
                }
            }
        }
    }
}
