package com.github.yuriisurzhykov.purs.domain.model

sealed interface LocationStatus {

    data class Open(
        val closeTime: String
    ) : LocationStatus

    data class ClosingSoon(
        val closeTime: String,
        val reopenTime: String
    ) : LocationStatus

    data class ClosedOpenSoon(
        val reopenTime: String
    ) : LocationStatus

    data class Closed(
        val openTime: String,
        val openDay: String
    ) : LocationStatus
}