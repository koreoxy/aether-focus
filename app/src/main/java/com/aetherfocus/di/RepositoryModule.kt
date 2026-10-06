package com.aetherfocus.di

import com.aetherfocus.data.repository.BlockedAppRepositoryImpl
import com.aetherfocus.data.repository.DistractionRepositoryImpl
import com.aetherfocus.data.repository.FocusSessionRepositoryImpl
import com.aetherfocus.domain.repository.BlockedAppRepository
import com.aetherfocus.domain.repository.DistractionRepository
import com.aetherfocus.domain.repository.FocusSessionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFocusSessionRepository(
        impl: FocusSessionRepositoryImpl
    ): FocusSessionRepository

    @Binds
    @Singleton
    abstract fun bindDistractionRepository(
        impl: DistractionRepositoryImpl
    ): DistractionRepository

    @Binds
    @Singleton
    abstract fun bindBlockedAppRepository(
        impl: BlockedAppRepositoryImpl
    ): BlockedAppRepository
}

