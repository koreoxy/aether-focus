package com.aetherfocus.domain.repository

import com.aetherfocus.core.model.BlockedApp
import kotlinx.coroutines.flow.Flow

interface BlockedAppRepository {
    fun getAllAppsFlow(): Flow<List<BlockedApp>>
    fun getBlockedPackageNamesFlow(): Flow<List<String>>

    suspend fun getBlockedPackageNames(): Set<String>
    suspend fun isPackageBlocked(packageName: String): Boolean
    suspend fun setAppBlocked(packageName: String, isBlocked: Boolean)
    suspend fun seedDefaultDistractionsIfNeeded()
}

