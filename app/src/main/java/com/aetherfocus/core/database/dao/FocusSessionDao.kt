package com.aetherfocus.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.aetherfocus.core.database.entity.FocusSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FocusSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: FocusSessionEntity): Long

    @Update
    suspend fun updateSession(session: FocusSessionEntity)

    @Query("SELECT * FROM focus_sessions WHERE id = :id LIMIT 1")
    suspend fun getSessionById(id: Long): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE status = 'IN_PROGRESS' ORDER BY started_at DESC LIMIT 1")
    suspend fun getActiveSession(): FocusSessionEntity?

    @Query("SELECT * FROM focus_sessions WHERE status = 'IN_PROGRESS' ORDER BY started_at DESC LIMIT 1")
    fun getActiveSessionFlow(): Flow<FocusSessionEntity?>

    @Query("SELECT * FROM focus_sessions ORDER BY started_at DESC")
    fun getAllSessionsFlow(): Flow<List<FocusSessionEntity>>

    @Query("SELECT * FROM focus_sessions WHERE status = 'COMPLETED' ORDER BY started_at DESC LIMIT :limit")
    fun getRecentCompletedSessions(limit: Int = 10): Flow<List<FocusSessionEntity>>

    @Query("UPDATE focus_sessions SET distraction_count = distraction_count + 1 WHERE id = :id")
    suspend fun incrementDistractionCount(id: Long)

    @Query("UPDATE focus_sessions SET actual_duration_seconds = :actualSeconds WHERE id = :id")
    suspend fun updateSessionProgress(id: Long, actualSeconds: Int)

    @Query("UPDATE focus_sessions SET status = :status, ended_at = :endedAt, actual_duration_seconds = :actualSeconds WHERE id = :id")
    suspend fun finalizeSession(id: Long, status: String, endedAt: Long, actualSeconds: Int)

    @Query("SELECT COALESCE(SUM(actual_duration_seconds), 0) FROM focus_sessions WHERE status = 'COMPLETED'")
    fun getTotalCompletedFocusSeconds(): Flow<Long>

    @Query("SELECT COUNT(*) FROM focus_sessions WHERE status = 'COMPLETED'")
    fun getCompletedSessionsCount(): Flow<Int>

    @Query("SELECT COALESCE(SUM(distraction_count), 0) FROM focus_sessions")
    fun getTotalDistractionsCount(): Flow<Int>

    @Query("DELETE FROM focus_sessions WHERE id = :id")
    suspend fun deleteSessionById(id: Long)
}

