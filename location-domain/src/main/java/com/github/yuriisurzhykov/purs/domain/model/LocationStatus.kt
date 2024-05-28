package com.github.yuriisurzhykov.purs.domain.model

import java.time.LocalTime

sealed interface LocationStatus {

    data class Open(
        val closeTime: LocalTime
    ) : LocationStatus

    data class Closing(
        val closeTime: LocalTime
    ) : LocationStatus

    data class ClosingSoon(
        val closeTime: LocalTime,
        val reopenTime: LocalTime
    ) : LocationStatus

    data class ClosingSoonLongReopen(
        val closeTime: LocalTime,
        val reopenDay: String,
        val reopenTime: LocalTime
    ) : LocationStatus

    data class ClosedOpenSoon(
        val reopenTime: LocalTime
    ) : LocationStatus

    data class Closed(
        val openTime: LocalTime,
        val openDay: String
    ) : LocationStatus

    data object ClosedFully : LocationStatus
}