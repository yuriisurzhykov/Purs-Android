package com.github.yuriisurzhykov.purs.data.cache

import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import kotlinx.coroutines.flow.Flow

interface LocationCacheDataSource {

    suspend fun fetchLocation(): Flow<LocationWithWorkingHours>

    suspend fun persistLocation(location: LocationWithWorkingHours)

    class Base(
        private val locationDao: LocationDao,
        private val getLocationId: GetLocationId
    ) : LocationCacheDataSource {
        override suspend fun fetchLocation(): Flow<LocationWithWorkingHours> {
            return locationDao.getLocation(getLocationId.locationId())
        }

        override suspend fun persistLocation(location: LocationWithWorkingHours) {
            locationDao.insert(location)
        }
    }
}