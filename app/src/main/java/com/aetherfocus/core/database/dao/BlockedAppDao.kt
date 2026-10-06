package com.aetherfocus.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aetherfocus.core.database.entity.BlockedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BlockedAppDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: BlockedAppEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<BlockedAppEntity>)

    @Query("SELECT * FROM blocked_apps ORDER BY app_name ASC")
    fun getAllAppsFlow(): Flow<List<BlockedAppEntity>>

    @Query("SELECT package_name FROM blocked_apps WHERE is_blocked = 1")
    suspend fun getBlockedPackageNames(): List<String>

    @Query("SELECT package_name FROM blocked_apps WHERE is_blocked = 1")
    fun getBlockedPackageNamesFlow(): Flow<List<String>>

    @Query("SELECT EXISTS(SELECT 1 FROM blocked_apps WHERE package_name = :packageName AND is_blocked = 1)")
    suspend fun isPackageBlocked(packageName: String): Boolean

    @Query("UPDATE blocked_apps SET is_blocked = :isBlocked WHERE package_name = :packageName")
    suspend fun updateBlockedState(packageName: String, isBlocked: Boolean)

    @Query("SELECT COUNT(*) FROM blocked_apps")
    suspend fun getAppCount(): Int
}

