package com.aetherfocus.data.repository

import com.aetherfocus.core.database.dao.FocusSessionDao
import com.aetherfocus.core.database.entity.FocusSessionEntity
import com.aetherfocus.core.model.FocusSession
import com.aetherfocus.core.model.InterventionTheme
import com.aetherfocus.core.model.SessionStats
import com.aetherfocus.core.model.SessionStatus
import com.aetherfocus.domain.repository.FocusSessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FocusSessionRepositoryImpl @Inject constructor(
    private val focusSessionDao: FocusSessionDao
) : FocusSessionRepository {

    override fun getActiveSessionFlow(): Flow<FocusSession?> {
        return focusSessionDao.getActiveSessionFlow().map { it?.toDomain() }
    }

    override fun getAllSessionsFlow(): Flow<List<FocusSession>> {
        return focusSessionDao.getAllSessionsFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun getRecentCompletedSessions(limit: Int): Flow<List<FocusSession>> {
        return focusSessionDao.getRecentCompletedSessions(limit).map { list -> list.map { it.toDomain() } }
    }

    override fun getSessionStatsFlow(): Flow<SessionStats> {
        return combine(
            focusSessionDao.getTotalCompletedFocusSeconds(),
            focusSessionDao.getCompletedSessionsCount(),
            focusSessionDao.getTotalDistractionsCount()
        ) { totalSeconds, completedCount, distractionCount ->
            SessionStats(
                totalFocusMinutes = totalSeconds / 60,
                completedSessionsCount = completedCount,
                totalDistractionsCount = distractionCount,
                currentStreakDays = if (completedCount > 0) 1 else 0,
                longestStreakDays = if (completedCount > 0) 1 else 0
            )
        }
    }

    override suspend fun getSessionById(id: Long): FocusSession? {
        return focusSessionDao.getSessionById(id)?.toDomain()
    }

    override suspend fun startSession(
        goalTitle: String,
        durationMinutes: Int,
        theme: InterventionTheme
    ): Long {
        val entity = FocusSessionEntity(
            goalTitle = goalTitle,
            targetDurationMinutes = durationMinutes,
            actualDurationSeconds = 0,
            startedAt = System.currentTimeMillis(),
            status = SessionStatus.IN_PROGRESS.name,
            distractionCount = 0,
            interventionTheme = theme.name
        )
        return focusSessionDao.insertSession(entity)
    }

    override suspend fun updateSessionProgress(sessionId: Long, elapsedSeconds: Int) {
        focusSessionDao.updateSessionProgress(sessionId, elapsedSeconds)
    }

    override suspend fun completeSession(sessionId: Long, actualSeconds: Int) {
        focusSessionDao.finalizeSession(
            id = sessionId,
            status = SessionStatus.COMPLETED.name,
            endedAt = System.currentTimeMillis(),
            actualSeconds = actualSeconds
        )
    }

    override suspend fun cancelSession(sessionId: Long, actualSeconds: Int) {
        focusSessionDao.finalizeSession(
            id = sessionId,
            status = SessionStatus.CANCELLED.name,
            endedAt = System.currentTimeMillis(),
            actualSeconds = actualSeconds
        )
    }

    override suspend fun incrementDistraction(sessionId: Long) {
        focusSessionDao.incrementDistractionCount(sessionId)
    }

    override suspend fun deleteSession(sessionId: Long) {
        focusSessionDao.deleteSessionById(sessionId)
    }
}

