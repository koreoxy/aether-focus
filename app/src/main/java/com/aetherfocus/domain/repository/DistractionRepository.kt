package com.aetherfocus.domain.repository

import com.aetherfocus.core.model.DistractionBreakdownItem
import com.aetherfocus.core.model.DistractionRecord
import com.aetherfocus.core.model.InterventionAction
import kotlinx.coroutines.flow.Flow

interface DistractionRepository {
    fun getRecordsForSession(sessionId: Long): Flow<List<DistractionRecord>>
    fun getTopDistractingApps(limit: Int = 5): Flow<List<DistractionBreakdownItem>>
    fun getRecentRecords(limit: Int = 20): Flow<List<DistractionRecord>>

    suspend fun recordDistraction(
        sessionId: Long,
        packageName: String,
        appName: String,
        actionTaken: InterventionAction
    ): Long

    suspend fun getDistractionCountForSession(sessionId: Long): Int
}

