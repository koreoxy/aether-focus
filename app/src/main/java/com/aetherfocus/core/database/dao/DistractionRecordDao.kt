package com.aetherfocus.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aetherfocus.core.database.entity.DistractionRecordEntity
import com.aetherfocus.core.model.DistractionBreakdownItem
import kotlinx.coroutines.flow.Flow

@Dao
interface DistractionRecordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: DistractionRecordEntity): Long

    @Query("SELECT * FROM distraction_records WHERE session_id = :sessionId ORDER BY timestamp DESC")
    fun getRecordsForSession(sessionId: Long): Flow<List<DistractionRecordEntity>>

    @Query(
        """
        SELECT package_name as packageName, app_name as appName, COUNT(*) as count 
        FROM distraction_records 
        GROUP BY package_name, app_name 
        ORDER BY count DESC 
        LIMIT :limit
        """
    )
    fun getTopDistractingApps(limit: Int = 5): Flow<List<DistractionBreakdownItem>>

    @Query("SELECT * FROM distraction_records ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentRecords(limit: Int = 20): Flow<List<DistractionRecordEntity>>

    @Query("SELECT COUNT(*) FROM distraction_records WHERE session_id = :sessionId")
    suspend fun getDistractionCountForSession(sessionId: Long): Int
}

