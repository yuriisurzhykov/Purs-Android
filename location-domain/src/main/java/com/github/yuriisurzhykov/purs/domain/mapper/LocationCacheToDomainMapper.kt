package com.github.yuriisurzhykov.purs.domain.mapper

import com.github.yuriisurzhykov.purs.core.Mapper
import com.github.yuriisurzhykov.purs.data.cache.model.LocationWithWorkingHours
import com.github.yuriisurzhykov.purs.domain.model.Location
import javax.inject.Inject

interface LocationCacheToDomainMapper : Mapper<LocationWithWorkingHours, Location> {

    class Base @Inject constructor(
        private val workingHourMapper: WorkingHourCacheToDomainMapper
    ) : LocationCacheToDomainMapper {
        override fun map(source: LocationWithWorkingHours): Location {
            return Location(
                source.location.locationName,
                workingHourMapper.map(source.workingHours)
            )
        }
    }
}