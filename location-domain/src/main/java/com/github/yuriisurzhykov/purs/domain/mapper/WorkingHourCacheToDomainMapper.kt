package com.github.yuriisurzhykov.purs.domain.mapper

import com.github.yuriisurzhykov.purs.core.Mapper
import com.github.yuriisurzhykov.purs.data.cache.model.WorkingHourCache
import com.github.yuriisurzhykov.purs.domain.model.TimeSlot
import com.github.yuriisurzhykov.purs.domain.model.WorkingHour
import javax.inject.Inject

interface WorkingHourCacheToDomainMapper :
    Mapper<List<WorkingHourCache>, Set<WorkingHour>> {

    class Base @Inject constructor(
        private val timeMapper: StringToLocalTimeMapper
    ) : WorkingHourCacheToDomainMapper {
        override fun map(source: List<WorkingHourCache>): Set<WorkingHour> {
            return source
                .groupBy { it.workingDayName }
                .asSequence()
                .map { entry ->
                    WorkingHour(
                        entry.key, entry.value
                            .map { hourCache ->
                                TimeSlot(
                                    timeMapper.map(hourCache.workingHourStart),
                                    timeMapper.map(hourCache.workingHourEnd)
                                )
                            }.toSet()
                    )
                }.toSet()
        }
    }
}