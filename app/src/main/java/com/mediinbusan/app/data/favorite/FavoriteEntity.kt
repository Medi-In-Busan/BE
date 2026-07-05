package com.mediinbusan.app.data.favorite

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteEntity(
    @PrimaryKey val itemId: String,
    val itemType: String, // FavoriteItemType.name ("HOSPITAL" | "PLACE")
    val name: String,
    val imageUrl: String?,
    val savedAt: Long
)
