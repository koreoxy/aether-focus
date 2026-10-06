package com.aetherfocus.domain.usecase

import com.aetherfocus.core.datastore.SessionPreferences
import javax.inject.Inject

class CheckDistractionUseCase @Inject constructor() {

    operator fun invoke(
        packageName: String,
        preferences: SessionPreferences
    ): Boolean {
        // Not active -> ignore
        if (!preferences.isFocusActive) return false

        // Currently in 5-minute snooze grace period -> ignore
        if (preferences.isInGracePeriod) return false

        // Own app -> ignore
        if (packageName == "com.aetherfocus") return false

        // Common system launcher/system UI packages -> ignore
        if (isSystemPackage(packageName)) return false

        // Check if package is in blocked list
        return preferences.blockedPackageNames.contains(packageName)
    }

    private fun isSystemPackage(packageName: String): Boolean {
        return packageName.startsWith("com.android.systemui") ||
                packageName.startsWith("com.google.android.apps.nexuslauncher") ||
                packageName.startsWith("com.android.launcher") ||
                packageName.startsWith("com.sec.android.app.launcher") ||
                packageName.startsWith("com.miui.home") ||
                packageName.startsWith("com.oppo.launcher")
    }
}

