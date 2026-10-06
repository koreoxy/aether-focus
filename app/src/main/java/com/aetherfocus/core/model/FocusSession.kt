package com.aetherfocus.core.model

data class FocusSession(
    val id: Long = 0,
    val goalTitle: String,
    val targetDurationMinutes: Int,
    val actualDurationSeconds: Int = 0,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null,
    val status: SessionStatus = SessionStatus.IN_PROGRESS,
    val distractionCount: Int = 0,
    val interventionTheme: InterventionTheme = InterventionTheme.MOTIVATIONAL
) {
    val isCompleted: Boolean
        get() = status == SessionStatus.COMPLETED

    val targetDurationSeconds: Int
        get() = targetDurationMinutes * 60

    val remainingSeconds: Int
        get() = (targetDurationSeconds - actualDurationSeconds).coerceAtLeast(0)

    val progressPercent: Float
        get() = if (targetDurationSeconds == 0) 1f
        else (actualDurationSeconds.toFloat() / targetDurationSeconds.toFloat()).coerceIn(0f, 1f)
}

