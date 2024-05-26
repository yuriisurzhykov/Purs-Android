package com.github.yuriisurzhykov.purs.data.cache

import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.github.yuriisurzhykov.purs.data.cache.model.LocationCache
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import com.github.yuriisurzhykov.purs.data.cache.model.WorkingHourCache
import kotlinx.coroutines.flow.Flow

abstract class LocationDao {

    @Transaction
    @Query("SELECT * FROM locations WHERE locationId=:id")
    abstract fun getLocation(id: Long): Flow<LocationWithWorkingHours>

    @Upsert
    abstract suspend fun insert(location: LocationCache): Long

    @Upsert
    abstract suspend fun insert(workingHour: WorkingHourCache): Long

    @Transaction
    suspend fun insert(location: LocationWithWorkingHours) {
        val locationId = insert(location.location)
        location.workingHours.forEach { workingHour ->
            insert(workingHour.copy(locationId = locationId))
        }
    }
}