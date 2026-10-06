package com.aetherfocus.data.repository

import com.aetherfocus.core.database.dao.DistractionRecordDao
import com.aetherfocus.core.database.entity.DistractionRecordEntity
import com.aetherfocus.core.model.DistractionBreakdownItem
import com.aetherfocus.core.model.DistractionRecord
import com.aetherfocus.core.model.InterventionAction
import com.aetherfocus.domain.repository.DistractionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DistractionRepositoryImpl @Inject constructor(
    private val distractionRecordDao: DistractionRecordDao
) : DistractionRepository {

    override fun getRecordsForSession(sessionId: Long): Flow<List<DistractionRecord>> {
        return distractionRecordDao.getRecordsForSession(sessionId).map { list -> list.map { it.toDomain() } }
    }

    override fun getTopDistractingApps(limit: Int): Flow<List<DistractionBreakdownItem>> {
        return distractionRecordDao.getTopDistractingApps(limit)
    }

    override fun getRecentRecords(limit: Int): Flow<List<DistractionRecord>> {
        return distractionRecordDao.getRecentRecords(limit).map { list -> list.map { it.toDomain() } }
    }

    override suspend fun recordDistraction(
        sessionId: Long,
        packageName: String,
        appName: String,
        actionTaken: InterventionAction
    ): Long {
        val entity = DistractionRecordEntity(
            sessionId = sessionId,
            packageName = packageName,
            appName = appName,
            timestamp = System.currentTimeMillis(),
            actionTaken = actionTaken.name
        )
        return distractionRecordDao.insertRecord(entity)
    }

    override suspend fun getDistractionCountForSession(sessionId: Long): Int {
        return distractionRecordDao.getDistractionCountForSession(sessionId)
    }
}

