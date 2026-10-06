package com.aetherfocus.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aetherfocus.core.model.BlockedApp

@Entity(tableName = "blocked_apps")
data class BlockedAppEntity(
    @PrimaryKey
    @ColumnInfo(name = "package_name")
    val packageName: String,

    @ColumnInfo(name = "app_name")
    val appName: String,

    @ColumnInfo(name = "is_blocked")
    val isBlocked: Boolean = true,

    @ColumnInfo(name = "is_default_distraction")
    val isDefaultDistraction: Boolean = false
) {
    fun toDomain(): BlockedApp = BlockedApp(
        packageName = packageName,
        appName = appName,
        isBlocked = isBlocked,
        isDefaultDistraction = isDefaultDistraction
    )

    companion object {
        fun fromDomain(domain: BlockedApp): BlockedAppEntity = BlockedAppEntity(
            packageName = domain.packageName,
            appName = domain.appName,
            isBlocked = domain.isBlocked,
            isDefaultDistraction = domain.isDefaultDistraction
        )
    }
}

