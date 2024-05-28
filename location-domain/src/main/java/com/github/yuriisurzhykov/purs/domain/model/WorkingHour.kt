package com.github.yuriisurzhykov.purs.domain.model

data class WorkingHour(
    val dayName: String,
    val scheduleList: Set<TimeSlot>
)