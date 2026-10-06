package com.aetherfocus.di

import android.content.Context
import androidx.room.Room
import com.aetherfocus.core.database.AetherDatabase
import com.aetherfocus.core.database.dao.BlockedAppDao
import com.aetherfocus.core.database.dao.DistractionRecordDao
import com.aetherfocus.core.database.dao.FocusSessionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAetherDatabase(
        @ApplicationContext context: Context
    ): AetherDatabase {
        return Room.databaseBuilder(
            context,
            AetherDatabase::class.java,
            AetherDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideFocusSessionDao(database: AetherDatabase): FocusSessionDao {
        return database.focusSessionDao()
    }

    @Provides
    fun provideDistractionRecordDao(database: AetherDatabase): DistractionRecordDao {
        return database.distractionRecordDao()
    }

    @Provides
    fun provideBlockedAppDao(database: AetherDatabase): BlockedAppDao {
        return database.blockedAppDao()
    }
}

