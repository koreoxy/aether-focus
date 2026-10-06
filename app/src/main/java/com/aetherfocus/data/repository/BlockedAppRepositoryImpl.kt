package com.aetherfocus.data.repository

import com.aetherfocus.core.database.dao.BlockedAppDao
import com.aetherfocus.core.database.entity.BlockedAppEntity
import com.aetherfocus.core.model.BlockedApp
import com.aetherfocus.domain.repository.BlockedAppRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BlockedAppRepositoryImpl @Inject constructor(
    private val blockedAppDao: BlockedAppDao
) : BlockedAppRepository {

    override fun getAllAppsFlow(): Flow<List<BlockedApp>> {
        return blockedAppDao.getAllAppsFlow().map { list -> list.map { it.toDomain() } }
    }

    override fun getBlockedPackageNamesFlow(): Flow<List<String>> {
        return blockedAppDao.getBlockedPackageNamesFlow()
    }

    override suspend fun getBlockedPackageNames(): Set<String> {
        return blockedAppDao.getBlockedPackageNames().toSet()
    }

    override suspend fun isPackageBlocked(packageName: String): Boolean {
        return blockedAppDao.isPackageBlocked(packageName)
    }

    override suspend fun setAppBlocked(packageName: String, isBlocked: Boolean) {
        blockedAppDao.updateBlockedState(packageName, isBlocked)
    }

    override suspend fun seedDefaultDistractionsIfNeeded() {
        if (blockedAppDao.getAppCount() == 0) {
            val defaultApps = listOf(
                BlockedAppEntity("com.zhiliaoapp.musically", "TikTok", isBlocked = true, isDefaultDistraction = true),
                BlockedAppEntity("com.ss.android.ugc.trill", "TikTok (Regional)", isBlocked = true, isDefaultDistraction = true),
                BlockedAppEntity("com.instagram.android", "Instagram", isBlocked = true, isDefaultDistraction = true),
                BlockedAppEntity("com.google.android.youtube", "YouTube", isBlocked = true, isDefaultDistraction = true),
                BlockedAppEntity("com.twitter.android", "X (Twitter)", isBlocked = true, isDefaultDistraction = true),
                BlockedAppEntity("com.facebook.katana", "Facebook", isBlocked = true, isDefaultDistraction = true),
                BlockedAppEntity("com.reddit.frontpage", "Reddit", isBlocked = true, isDefaultDistraction = true)
            )
            blockedAppDao.insertAll(defaultApps)
        }
    }
}

