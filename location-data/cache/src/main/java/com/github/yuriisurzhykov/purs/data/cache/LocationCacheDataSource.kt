package com.github.yuriisurzhykov.purs.data.cache

import com.github.yuriisurzhykov.purs.core.RequestResult
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface LocationCacheDataSource {

    suspend fun fetchLocation(): Flow<RequestResult<LocationWithWorkingHours>>

    suspend fun persistLocation(location: LocationWithWorkingHours)

    class Base @Inject constructor(
        private val locationDao: LocationDao,
        private val getLocationId: GetLocationId
    ) : LocationCacheDataSource {

        override suspend fun fetchLocation(): Flow<RequestResult<LocationWithWorkingHours>> {
            val requestLocationId = getLocationId.locationId()
            return flow { emit(locationDao.getLocation(requestLocationId)) }
                .map { location ->
                    if (location != null) {
                        RequestResult.Success(location)
                    } else {
                        RequestResult.Error(
                            null,
                            NoValueFoundException("Location with ID $requestLocationId not found")
                        )
                    }
                }.catch {
                    emit(RequestResult.Error(null, it))
                }

        }

        override suspend fun persistLocation(location: LocationWithWorkingHours) {
            locationDao.delete(getLocationId.locationId())
            val newLocation = location.location.copy(locationId = getLocationId.locationId())
            locationDao.insert(location.copy(location = newLocation))
        }
    }
}