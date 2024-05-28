package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import java.time.DayOfWeek
import javax.inject.Inject

/**
 * In the result list of working days, some days may be missing. So this interface
 * appends all missing days to the collection and returns the result.
 * */
interface AddMissingDaysUseCase : ProcessWorkingHoursCollection {

    class Base @Inject constructor() : AddMissingDaysUseCase {
        override fun process(collection: Collection<WorkingDay>): Collection<WorkingDay> {
            val daysOfWeek = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            val existingDays = collection.map { it.dayName }.toSet()

            val missingDays = daysOfWeek.filter { it !in existingDays }.map {
                WorkingDay(it, DayOfWeek.of(daysOfWeek.indexOf(it) + 1), emptySet())
            }

            return collection + missingDays
        }
    }
}