package com.aetherfocus.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aetherfocus.core.model.FocusSession
import com.aetherfocus.core.model.InterventionTheme
import com.aetherfocus.core.model.SessionStatus

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "goal_title")
    val goalTitle: String,

    @ColumnInfo(name = "target_duration_minutes")
    val targetDurationMinutes: Int,

    @ColumnInfo(name = "actual_duration_seconds")
    val actualDurationSeconds: Int = 0,

    @ColumnInfo(name = "started_at")
    val startedAt: Long,

    @ColumnInfo(name = "ended_at")
    val endedAt: Long? = null,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "distraction_count")
    val distractionCount: Int = 0,

    @ColumnInfo(name = "intervention_theme")
    val interventionTheme: String = InterventionTheme.MOTIVATIONAL.name
) {
    fun toDomain(): FocusSession = FocusSession(
        id = id,
        goalTitle = goalTitle,
        targetDurationMinutes = targetDurationMinutes,
        actualDurationSeconds = actualDurationSeconds,
        startedAt = startedAt,
        endedAt = endedAt,
        status = runCatching { SessionStatus.valueOf(status) }.getOrDefault(SessionStatus.IN_PROGRESS),
        distractionCount = distractionCount,
        interventionTheme = runCatching { InterventionTheme.valueOf(interventionTheme) }.getOrDefault(InterventionTheme.MOTIVATIONAL)
    )

    companion object {
        fun fromDomain(domain: FocusSession): FocusSessionEntity = FocusSessionEntity(
            id = domain.id,
            goalTitle = domain.goalTitle,
            targetDurationMinutes = domain.targetDurationMinutes,
            actualDurationSeconds = domain.actualDurationSeconds,
            startedAt = domain.startedAt,
            endedAt = domain.endedAt,
            status = domain.status.name,
            distractionCount = domain.distractionCount,
            interventionTheme = domain.interventionTheme.name
        )
    }
}

