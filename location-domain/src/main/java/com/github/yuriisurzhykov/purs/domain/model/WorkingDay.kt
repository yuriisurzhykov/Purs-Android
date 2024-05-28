package com.github.yuriisurzhykov.purs.domain.model

import java.time.DayOfWeek

data class WorkingDay(
    val dayName: String,
    val weekDay: DayOfWeek,
    val scheduleList: Set<TimeSlot>
) {
    fun open24H() = scheduleList.any { it.isOpen24H() }
}