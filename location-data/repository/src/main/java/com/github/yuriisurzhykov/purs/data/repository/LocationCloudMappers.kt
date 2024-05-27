package com.github.yuriisurzhykov.purs.data.repository

import com.github.yuriisurzhykov.purs.core.Mapper
import com.github.yuriisurzhykov.purs.data.cache.GetLocationId
import com.github.yuriisurzhykov.purs.data.cache.model.LocationCache
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import com.github.yuriisurzhykov.purs.data.cache.model.WorkingHourCache
import com.github.yuriisurzhykov.purs.data.cloud.model.LocationCloud
import com.github.yuriisurzhykov.purs.data.cloud.model.WorkingHourCloud
import javax.inject.Inject

interface LocationCloudToCacheMapper : Mapper<LocationCloud, LocationWithWorkingHours> {

    class Base @Inject constructor(
        private val cloudMapper: LocationCloudMapper,
        private val workingHoursCloudMapper: LocationWorkingHoursCloudMapper
    ) : LocationCloudToCacheMapper {
        override fun map(source: LocationCloud): LocationWithWorkingHours {
            val sourceLocation: LocationCache = source.map(cloudMapper)
            val workingHours: List<WorkingHourCache> = source.map(workingHoursCloudMapper)
            return LocationWithWorkingHours(sourceLocation, workingHours)
        }
    }
}

class LocationCloudMapper @Inject constructor(
    private val getLocationId: GetLocationId
) : LocationCloud.Mapper<LocationCache> {
    override fun map(locationName: String, workingHours: List<WorkingHourCloud>) =
        LocationCache(getLocationId.locationId(), locationName)
}

class LocationWorkingHoursCloudMapper @Inject constructor(
    private val mapper: WorkingHoursCloudMapper
) : LocationCloud.Mapper<List<WorkingHourCache>> {
    override fun map(
        locationName: String,
        workingHours: List<WorkingHourCloud>
    ): List<WorkingHourCache> =
        workingHours.map { workingHoursCloud -> workingHoursCloud.map(mapper) }
}

class WorkingHoursCloudMapper @Inject constructor() : WorkingHourCloud.Mapper<WorkingHourCache> {
    override fun map(dayName: String, startTime: String, endTime: String): WorkingHourCache {
        return WorkingHourCache(0, startTime, endTime, dayName, 0)
    }
}