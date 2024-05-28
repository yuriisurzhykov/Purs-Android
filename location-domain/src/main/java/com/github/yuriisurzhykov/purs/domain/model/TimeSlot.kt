package com.github.yuriisurzhykov.purs.domain.model

import java.time.LocalTime

data class TimeSlot(
    val startTime: LocalTime,
    val endTime: LocalTime
) {
    fun isOpen24H() = startTime == LocalTime.MIDNIGHT && endTime == LocalTime.MIDNIGHT
}
