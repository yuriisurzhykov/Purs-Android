package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingHour
import javax.inject.Inject

interface SortMissingDaysUseCase : ProcessWorkingHoursCollection {

    class Base @Inject constructor() : SortMissingDaysUseCase {
        override fun process(collection: Collection<WorkingHour>): Collection<WorkingHour> {
            val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            val dayToIndex = daysOfWeek.withIndex().associate { it.value to it.index }
            return collection.map { workingHour ->
                workingHour.scheduleList.sortedWith(compareBy { it.startTime })
                workingHour
            }.sortedWith(compareBy { dayToIndex[it.dayName] })
        }
    }
}