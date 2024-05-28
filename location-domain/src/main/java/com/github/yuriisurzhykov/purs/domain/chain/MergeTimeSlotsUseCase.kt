package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.TimeSlot
import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import javax.inject.Inject

/**
 * This interface defines a use case for merging time slots in a collection of working days that
 * overlap each other. For example, time slots: 8:00-15:00 and 10:00-11:00 are considered overlapping
 * time slots. To this interface merges all such time slots into one time slot: 8:00-15:00.
 * */
interface MergeTimeSlotsUseCase {

    fun mergeTimeSlots(workDay: WorkingDay): WorkingDay

    class Base @Inject constructor() : MergeTimeSlotsUseCase {
        override fun mergeTimeSlots(workDay: WorkingDay): WorkingDay {
            val timeSlots = workDay.scheduleList
            if (timeSlots.isEmpty()) return workDay

            // Sort time intervals by day and start time
            val sortedTimeSlots = timeSlots.sortedWith(compareBy { it.startTime })

            val mergedTimeSlots = mutableSetOf<TimeSlot>()

            var currentSlot = sortedTimeSlots[0]

            for (i in 1 until sortedTimeSlots.size) {
                val nextSlot = sortedTimeSlots[i]

                // Check if current and the next time intervals are intersect
                if (currentSlot.endTime >= nextSlot.startTime) {
                    // Merge intervals
                    currentSlot = TimeSlot(
                        startTime = currentSlot.startTime,
                        endTime = maxOf(currentSlot.endTime, nextSlot.endTime)
                    )
                } else {
                    // Add current interval to the list of merged intervals
                    mergedTimeSlots.add(currentSlot)
                    // Switch to the next interval
                    currentSlot = nextSlot
                }
            }

            // Add the last interval
            mergedTimeSlots.add(currentSlot)

            return workDay.copy(scheduleList = mergedTimeSlots)
        }
    }
}