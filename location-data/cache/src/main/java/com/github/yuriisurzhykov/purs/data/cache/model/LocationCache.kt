package com.github.yuriisurzhykov.purs.data.cache.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationCache(
    @PrimaryKey(autoGenerate = false)
    val locationId: Long,
    val locationName: String
)