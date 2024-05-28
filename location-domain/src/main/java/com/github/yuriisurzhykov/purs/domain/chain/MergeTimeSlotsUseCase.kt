package com.github.yuriisurzhykov.purs.domain.chain

import com.github.yuriisurzhykov.purs.domain.model.TimeSlot
import com.github.yuriisurzhykov.purs.domain.model.WorkingHour
import javax.inject.Inject

interface MergeTimeSlotsUseCase {

    fun mergeTimeSlots(workDay: WorkingHour): WorkingHour

    class Base @Inject constructor() : MergeTimeSlotsUseCase {
        override fun mergeTimeSlots(workDay: WorkingHour): WorkingHour {
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