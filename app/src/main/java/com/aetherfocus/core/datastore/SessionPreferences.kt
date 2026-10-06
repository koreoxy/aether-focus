package com.aetherfocus.core.datastore

import com.aetherfocus.core.model.InterventionTheme

data class SessionPreferences(
    val isFocusActive: Boolean = false,
    val activeSessionId: Long = 0L,
    val activeGoalTitle: String = "",
    val sessionStartTime: Long = 0L,
    val sessionTargetDurationMinutes: Int = 25,
    val gracePeriodEndTime: Long = 0L,
    val blockedPackageNames: Set<String> = emptySet(),
    val interventionTheme: InterventionTheme = InterventionTheme.MOTIVATIONAL,
    val soundEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
) {
    val isInGracePeriod: Boolean
        get() = System.currentTimeMillis() < gracePeriodEndTime
}

