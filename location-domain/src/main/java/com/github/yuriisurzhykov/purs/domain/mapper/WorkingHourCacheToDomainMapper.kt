package com.github.yuriisurzhykov.purs.domain.mapper

import com.github.yuriisurzhykov.purs.core.Mapper
import com.github.yuriisurzhykov.purs.data.cache.model.WorkingHourCache
import com.github.yuriisurzhykov.purs.domain.chain.MergeTimeSlotsUseCase
import com.github.yuriisurzhykov.purs.domain.model.TimeSlot
import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import javax.inject.Inject

interface WorkingHourCacheToDomainMapper :
    Mapper<List<WorkingHourCache>, Set<WorkingDay>> {

    class Base @Inject constructor(
        private val timeMapper: StringToLocalTimeMapper,
        private val mergeTimeSlotsUseCase: MergeTimeSlotsUseCase,
        private val weekDayMapper: StringToDayOfWeekMapper
    ) : WorkingHourCacheToDomainMapper {
        override fun map(source: List<WorkingHourCache>): Set<WorkingDay> {
            return source
                .groupBy { it.workingDayName }
                .asSequence()
                .map { entry ->
                    val workingDay = WorkingDay(
                        entry.key,
                        weekDayMapper.map(entry.key),
                        entry.value.map { hourCache ->
                            TimeSlot(
                                timeMapper.map(hourCache.workingHourStart),
                                timeMapper.map(hourCache.workingHourEnd)
                            )
                        }.toSet()
                    )
                    mergeTimeSlotsUseCase.mergeTimeSlots(workingDay)
                }.toSet()
        }
    }
}