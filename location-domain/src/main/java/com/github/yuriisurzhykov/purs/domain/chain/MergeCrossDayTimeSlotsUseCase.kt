package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import java.time.LocalTime
import javax.inject.Inject

/**
 * The chain processor for merging cross-day time slots. This class is responsible for merging
 * working days that contain time slots that cross days. For example, if the starting time of
 * the first time slot is 20:00 and the ending time is 24:00 but the starting time of next day
 * is 00:00 and the ending time is 04:00, then the first time slot will be merged with the
 * next day's time slot. So the merged time slots will be: 20:00-04:00. And will be removed from
 * the next schedule day.
 * */
interface MergeCrossDayTimeSlotsUseCase : ProcessWorkingHoursCollection {

    class Base @Inject constructor(
        private val sortWorkingDaysUseCase: SortWorkingDaysUseCase
    ) : MergeCrossDayTimeSlotsUseCase {

        override fun process(collection: Collection<WorkingDay>): Collection<WorkingDay> {
            if (collection.isEmpty() || collection.size == 1) return collection

            val mergedTimeSlots = mutableSetOf<WorkingDay>()
            val sortedTimeSlots = sortWorkingDaysUseCase.process(collection).iterator()

            // Get the first time slot
            var currentSlot = sortedTimeSlots.next()

            // Iterate through the sorted time slots and merge cross-day time slots
            while (sortedTimeSlots.hasNext()) {
                val nextSlot = sortedTimeSlots.next()
                val lastCurrentTimeSlot = currentSlot.scheduleList.last()
                val firstNextTimeSlot = nextSlot.scheduleList.first()

                // Merge the current time slot with the next time slot if they are on different days
                // and the last current time slot ends at midnight and the first next time slot starts at midnight
                if (currentSlot.dayName != nextSlot.dayName &&
                    lastCurrentTimeSlot.endTime == LocalTime.MIDNIGHT &&
                    firstNextTimeSlot.startTime == LocalTime.MIDNIGHT &&
                    !lastCurrentTimeSlot.isOpen24H()
                ) {
                    val timeSlotSize = currentSlot.scheduleList.size
                    // Update the working set of the current time slot to exclude the last time slot
                    val updatedWorkingSet =
                        currentSlot.scheduleList.take(timeSlotSize - 1).toMutableSet()
                    // Add the next time slot to the working set
                    updatedWorkingSet.add(
                        lastCurrentTimeSlot.copy(
                            startTime = lastCurrentTimeSlot.startTime,
                            endTime = firstNextTimeSlot.endTime
                        )
                    )
                    // Add the merged time slot to the merged time slots
                    mergedTimeSlots.add(currentSlot.copy(scheduleList = updatedWorkingSet))
                    // Update the current time slot to the next time slot
                    currentSlot =
                        nextSlot.copy(scheduleList = nextSlot.scheduleList.drop(1).toSet())
                } else {
                    // Add the current time slot to the merged time slots
                    mergedTimeSlots.add(currentSlot)
                    currentSlot = nextSlot
                }
            }
            mergedTimeSlots.add(currentSlot)
            return mergedTimeSlots
        }
    }
}