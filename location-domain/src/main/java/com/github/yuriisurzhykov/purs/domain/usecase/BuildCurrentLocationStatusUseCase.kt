package com.github.yuriisurzhykov.purs.domain.usecase

import com.github.yuriisurzhykov.purs.domain.model.LocationStatus
import com.github.yuriisurzhykov.purs.domain.model.TimeSlot
import com.github.yuriisurzhykov.purs.domain.model.WorkingDay
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import javax.inject.Inject

interface BuildCurrentLocationStatusUseCase {

    fun currentStatus(workingDays: Collection<WorkingDay>): LocationStatus

    class Base @Inject constructor() : BuildCurrentLocationStatusUseCase {
        override fun currentStatus(workingDays: Collection<WorkingDay>): LocationStatus {
            return generateStatusMessage(workingDays.toSet())
        }

        private fun generateStatusMessage(
            workingDays: Set<WorkingDay>,
        ): LocationStatus {
            val currentDate = LocalDate.now()
            val currentTime = LocalTime.now()
            // Get current schedule for the day
            val currentSchedule = workingDays.toList()[currentDate.dayOfWeek.value - 1]
            // Get next schedule for the current day or next day
            val nextSchedule: Pair<String, TimeSlot>? =
                findNextWorkingDay(workingDays.toList(), currentDate, currentTime)

            // Return location status for whether it is open or closed now
            return findCurrentOpenStatus(currentSchedule, nextSchedule, currentTime)
                ?: findClosedStatus(currentTime, nextSchedule)
        }

        /**
         * Returns the location status if the location is closed currently. It looks for next
         * schedule and returns [LocationStatus] based if it opens within 24 hours or not.
         * */
        private fun findClosedStatus(
            currentTime: LocalTime,
            nextSchedule: Pair<String, TimeSlot>?
        ): LocationStatus {

            if (nextSchedule == null) {
                return LocationStatus.ClosedFully
            }

            val nextOpenTime = nextSchedule.second.startTime
            val reopenTimeDifference = currentTime.until(nextOpenTime, ChronoUnit.HOURS)
            return if (reopenTimeDifference > 24) {
                LocationStatus.Closed(nextOpenTime, nextSchedule.first)
            } else {
                LocationStatus.ClosedOpenSoon(nextOpenTime)
            }
        }

        /**
         * Returns the location status if the location is open currently. It looks for next
         * schedule and returns [LocationStatus] based if it REopens within 24 hours or not.
         * */
        private fun findCurrentOpenStatus(
            workingDay: WorkingDay,
            nextWorkingDay: Pair<String, TimeSlot>?,
            currentTime: LocalTime
        ): LocationStatus? {
            // Looking for schedule time slot that applied to the current time
            val currentOpenSchedule: TimeSlot? = workingDay.scheduleList.find { timeSlot ->
                if (timeSlot.endTime < timeSlot.startTime) {
                    timeSlot.startTime.isBefore(currentTime)
                } else {
                    timeSlot.endTime.isAfter(currentTime) && timeSlot.startTime.isBefore(currentTime)
                }
            }
            return if (currentOpenSchedule != null) {
                val timeDifference =
                    if (currentOpenSchedule.endTime < currentOpenSchedule.startTime) {
                        currentTime.until(LocalTime.MAX, ChronoUnit.MINUTES) +
                                LocalTime.MIDNIGHT.until(currentOpenSchedule.endTime, ChronoUnit.MINUTES)
                    } else {
                        currentTime.until(currentOpenSchedule.endTime, ChronoUnit.MINUTES)
                    }
                // If the location closes within 24 hours, return the location status
                // that it closing soon. Otherwise, return the location status that it opens
                return if (timeDifference <= 60) {
                    if (nextWorkingDay == null) {
                        return LocationStatus.Closing(currentOpenSchedule.endTime)
                    }

                    val nextOpenTime = nextWorkingDay.second.startTime
                    val reopenTimeDifference = currentTime.until(nextOpenTime, ChronoUnit.HOURS)
                    if (reopenTimeDifference > 24) {
                        LocationStatus.ClosingSoonLongReopen(
                            currentOpenSchedule.endTime,
                            nextWorkingDay.first,
                            nextOpenTime
                        )
                    } else {
                        LocationStatus.ClosingSoon(currentOpenSchedule.endTime, nextOpenTime)
                    }
                } else {
                    LocationStatus.Open(currentOpenSchedule.endTime)
                }
            } else null
        }

        private fun findNextWorkingDay(
            schedule: List<WorkingDay>,
            currentDate: LocalDate,
            currentTime: LocalTime
        ): Pair<String, TimeSlot>? {
            var currentIndex = currentDate.dayOfWeek.value - 1
            var firstLoopRun = true
            while (true) {
                // Check if we have reached the start point for the work day. Start point means
                // that we reached the current day of the week. If we have reached the start point,
                // we either returns next time slot or null
                if (currentIndex == currentDate.dayOfWeek.value - 1) {
                    val containsSchedule =
                        schedule[currentIndex].scheduleList.find { it.startTime.isAfter(currentTime) }
                    return if (containsSchedule != null) {
                        Pair(schedule[currentIndex].dayName, containsSchedule)
                    } else {
                        if (firstLoopRun) {
                            firstLoopRun = false
                            continue
                        } else {
                            return null
                        }
                    }
                }
                // If we found any other time schedule that is not empty, return it
                if (schedule[currentIndex].scheduleList.isNotEmpty()) {
                    return Pair(
                        schedule[currentIndex].dayName,
                        schedule[currentIndex].scheduleList.first()
                    )
                }
                // Increment by one in range of 0 to 6
                currentIndex = (currentIndex + 1) % DayOfWeek.entries.size
            }
        }
    }
}

