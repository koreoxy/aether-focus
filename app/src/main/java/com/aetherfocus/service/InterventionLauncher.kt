package com.aetherfocus.service

import android.content.Context
import android.content.Intent
import com.aetherfocus.core.common.PermissionUtils
import com.aetherfocus.feature.intervention.InterventionActivity

object InterventionLauncher {

    const val EXTRA_BLOCKED_PACKAGE = "extra_blocked_package"
    const val EXTRA_APP_NAME = "extra_app_name"

    fun launchIntervention(
        context: Context,
        packageName: String,
        appName: String
    ) {
        val intent = Intent(context, InterventionActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_SINGLE_TOP
            putExtra(EXTRA_BLOCKED_PACKAGE, packageName)
            putExtra(EXTRA_APP_NAME, appName)
        }
        context.startActivity(intent)
    }
}

