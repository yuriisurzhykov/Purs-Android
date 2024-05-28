package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingHour
import javax.inject.Inject

interface AddMissingDaysUseCase : ProcessWorkingHoursCollection {

    class Base @Inject constructor() : AddMissingDaysUseCase {
        override fun process(collection: Collection<WorkingHour>): Collection<WorkingHour> {
            val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            val existingDays = collection.map { it.dayName }.toSet()

            val missingDays = daysOfWeek.filter { it !in existingDays }.map {
                WorkingHour(it, emptySet())
            }

            return collection + missingDays
        }
    }
}