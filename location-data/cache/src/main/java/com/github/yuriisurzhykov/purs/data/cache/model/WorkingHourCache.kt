package com.github.yuriisurzhykov.purs.data.cache.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "working_hours",
    foreignKeys = [ForeignKey(
        entity = LocationCache::class,
        parentColumns = ["locationId"],
        childColumns = ["locationId"],
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class WorkingHourCache(
    @PrimaryKey(autoGenerate = true)
    val workingHourId: Long,
    val workingHourStart: String,
    val workingHourEnd: String,
    val workingDayName: String,
    val locationId: Long
)