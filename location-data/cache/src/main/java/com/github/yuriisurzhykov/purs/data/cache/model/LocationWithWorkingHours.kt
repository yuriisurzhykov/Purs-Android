package com.github.yuriisurzhykov.purs.data.cache.model

import androidx.room.Embedded
import androidx.room.Relation

data class LocationWithWorkingHours(
    @Embedded
    val location: LocationCache,
    @Relation(
        parentColumn = "locationId",    // Primary key in LocationCache table
        entityColumn = "locationId"     // Foreign key in WorkingHourCache table
    )
    val workingHours: List<WorkingHourCache>
)