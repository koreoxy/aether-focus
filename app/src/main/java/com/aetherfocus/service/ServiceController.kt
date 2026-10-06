package com.aetherfocus.service

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat

object ServiceController {

    fun startGuardian(context: Context) {
        val intent = Intent(context, FocusGuardianService::class.java).apply {
            action = FocusGuardianService.ACTION_START_GUARDIAN
        }
        ContextCompat.startForegroundService(context, intent)
    }

    fun stopGuardian(context: Context) {
        val intent = Intent(context, FocusGuardianService::class.java).apply {
            action = FocusGuardianService.ACTION_STOP_GUARDIAN
        }
        context.startService(intent)
    }
}

