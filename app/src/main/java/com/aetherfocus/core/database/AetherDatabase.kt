package com.aetherfocus.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aetherfocus.core.database.dao.BlockedAppDao
import com.aetherfocus.core.database.dao.DistractionRecordDao
import com.aetherfocus.core.database.dao.FocusSessionDao
import com.aetherfocus.core.database.entity.BlockedAppEntity
import com.aetherfocus.core.database.entity.DistractionRecordEntity
import com.aetherfocus.core.database.entity.FocusSessionEntity

@Database(
    entities = [
        FocusSessionEntity::class,
        DistractionRecordEntity::class,
        BlockedAppEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AetherDatabase : RoomDatabase() {
    abstract fun focusSessionDao(): FocusSessionDao
    abstract fun distractionRecordDao(): DistractionRecordDao
    abstract fun blockedAppDao(): BlockedAppDao

    companion object {
        const val DATABASE_NAME = "aether_focus_db"
    }
}

