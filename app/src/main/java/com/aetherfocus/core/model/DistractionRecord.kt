package com.aetherfocus.core.model

data class DistractionRecord(
    val id: Long = 0,
    val sessionId: Long,
    val packageName: String,
    val appName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val actionTaken: InterventionAction = InterventionAction.BACK_TO_WORK
)

data class SessionStats(
    val totalFocusMinutes: Long = 0,
    val completedSessionsCount: Int = 0,
    val totalDistractionsCount: Int = 0,
    val currentStreakDays: Int = 0,
    val longestStreakDays: Int = 0
)

data class DistractionBreakdownItem(
    val packageName: String,
    val appName: String,
    val count: Int
)

