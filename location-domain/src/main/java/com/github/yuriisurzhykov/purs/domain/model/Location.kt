package com.github.yuriisurzhykov.purs.domain.model

data class Location(
    val locationName: String,
    val status: LocationStatus?,
    val workingDays: Collection<WorkingDay>
)