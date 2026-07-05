package com.mediinbusan.app.data.recent

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recently_viewed")
data class RecentlyViewedEntity(
    @PrimaryKey val itemId: String,
    val itemName: String,
    val viewedAt: Long
)
