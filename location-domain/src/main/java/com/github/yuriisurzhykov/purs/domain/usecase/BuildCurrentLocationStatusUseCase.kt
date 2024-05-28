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
            val currentSchedule = workingDays.toList()[currentDate.dayOfWeek.value - 1]
            val nextSchedule: Pair<String, TimeSlot>? =
                findNextWorkingDay(workingDays.toList(), currentDate, currentTime)

            return findCurrentOpenStatus(currentSchedule, nextSchedule!!, currentTime)
                ?: findClosedStatus(currentTime, nextSchedule)
        }

        private fun findClosedStatus(
            currentTime: LocalTime,
            nextSchedule: Pair<String, TimeSlot>
        ): LocationStatus {
            val nextOpenTime = nextSchedule.second.startTime
            val reopenTimeDifference = currentTime.until(nextOpenTime, ChronoUnit.HOURS)
            return if (reopenTimeDifference > 24) {
                LocationStatus.Closed(nextOpenTime, nextSchedule.first)
            } else {
                LocationStatus.ClosedOpenSoon(nextOpenTime)
            }
        }

        private fun findCurrentOpenStatus(
            workingDay: WorkingDay,
            nextWorkingDay: Pair<String, TimeSlot>,
            currentTime: LocalTime
        ): LocationStatus? {
            val currentOpenSchedule: TimeSlot? = workingDay.scheduleList.find { timeSlot ->
                if (timeSlot.endTime < timeSlot.startTime) {
                    timeSlot.startTime.isBefore(currentTime)
                } else {
                    timeSlot.endTime.isAfter(currentTime) && timeSlot.startTime.isBefore(currentTime)
                }
            }
            return if (currentOpenSchedule != null) {
                val timeDifference =
                    currentOpenSchedule.endTime.until(currentTime, ChronoUnit.MINUTES)
                return if (timeDifference <= 60) {
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
            var currentIndex = currentDate.dayOfWeek.value
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
                        return null
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

