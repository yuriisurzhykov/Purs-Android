package com.github.yuriisurzhykov.purs.domain.usecase

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle
import java.util.Locale

interface GetNow {
    fun getDayName(): String
    fun getCurrentTime(): LocalTime

    class Base : GetNow {
        override fun getDayName(): String =
            LocalDate.now().dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())

        override fun getCurrentTime(): LocalTime = LocalTime.now()
    }
}