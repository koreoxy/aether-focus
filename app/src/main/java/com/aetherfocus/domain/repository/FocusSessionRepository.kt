package com.aetherfocus.domain.repository

import com.aetherfocus.core.model.FocusSession
import com.aetherfocus.core.model.InterventionTheme
import com.aetherfocus.core.model.SessionStats
import kotlinx.coroutines.flow.Flow

interface FocusSessionRepository {
    fun getActiveSessionFlow(): Flow<FocusSession?>
    fun getAllSessionsFlow(): Flow<List<FocusSession>>
    fun getRecentCompletedSessions(limit: Int = 10): Flow<List<FocusSession>>
    fun getSessionStatsFlow(): Flow<SessionStats>

    suspend fun getSessionById(id: Long): FocusSession?
    suspend fun startSession(goalTitle: String, durationMinutes: Int, theme: InterventionTheme): Long
    suspend fun updateSessionProgress(sessionId: Long, elapsedSeconds: Int)
    suspend fun completeSession(sessionId: Long, actualSeconds: Int)
    suspend fun cancelSession(sessionId: Long, actualSeconds: Int)
    suspend fun incrementDistraction(sessionId: Long)
    suspend fun deleteSession(sessionId: Long)
}

