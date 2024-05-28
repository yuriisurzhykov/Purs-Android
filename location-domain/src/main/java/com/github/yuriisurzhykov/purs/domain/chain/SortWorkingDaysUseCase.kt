package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import javax.inject.Inject

/**
 * Chain processor for the list of working hours collection. It sorts all working days by day of week
 * based on the day name and then by start time.
 * */
interface SortWorkingDaysUseCase : ProcessWorkingHoursCollection {

    class Base @Inject constructor() : SortWorkingDaysUseCase {
        override fun process(collection: Collection<WorkingDay>): Collection<WorkingDay> {
            val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            val dayToIndex = daysOfWeek.withIndex().associate { it.value to it.index }
            return collection.map { workingHour ->
                workingHour.scheduleList.sortedWith(compareBy { it.startTime })
                workingHour
            }.sortedWith(compareBy { dayToIndex[it.dayName] })
        }
    }
}