package com.aetherfocus.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class ScreenStateReceiver(
    private val onScreenStateChanged: (isScreenOn: Boolean) -> Unit
) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SCREEN_ON -> onScreenStateChanged(true)
            Intent.ACTION_SCREEN_OFF -> onScreenStateChanged(false)
        }
    }

    companion object {
        fun createFilter(): IntentFilter {
            return IntentFilter().apply {
                addAction(Intent.ACTION_SCREEN_ON)
                addAction(Intent.ACTION_SCREEN_OFF)
            }
        }
    }
}

