package com.aetherfocus.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.aetherfocus.core.model.DistractionRecord
import com.aetherfocus.core.model.InterventionAction

@Entity(
    tableName = "distraction_records",
    foreignKeys = [
        ForeignKey(
            entity = FocusSessionEntity::class,
            parentColumns = ["id"],
            childColumns = ["session_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["session_id"]),
        Index(value = ["package_name"])
    ]
)
data class DistractionRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "session_id")
    val sessionId: Long,

    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: Long,

    @ColumnInfo(name = "action_taken")
    val actionTaken: String
) {
    fun toDomain(): DistractionRecord = DistractionRecord(
        id = id,
        sessionId = sessionId,
        packageName = packageName,
        appName = appName,
        timestamp = timestamp,
        actionTaken = runCatching { InterventionAction.valueOf(actionTaken) }.getOrDefault(InterventionAction.BACK_TO_WORK)
    )

    companion object {
        fun fromDomain(domain: DistractionRecord): DistractionRecordEntity = DistractionRecordEntity(
            id = domain.id,
            sessionId = domain.sessionId,
            packageName = domain.packageName,
            appName = domain.appName,
            timestamp = domain.timestamp,
            actionTaken = domain.actionTaken.name
        )
    }
}

