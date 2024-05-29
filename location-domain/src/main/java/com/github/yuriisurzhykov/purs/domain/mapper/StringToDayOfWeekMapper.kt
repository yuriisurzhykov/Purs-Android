package com.github.yuriisurzhykov.purs.domain.mapper

import com.github.yuriisurzhykov.purs.core.Mapper
import java.time.DayOfWeek
import javax.inject.Inject

interface StringToDayOfWeekMapper : Mapper<String, DayOfWeek> {

    class Base @Inject constructor() : StringToDayOfWeekMapper {
        override fun map(source: String): DayOfWeek {
            val weekNames = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            return DayOfWeek.of(weekNames.indexOf(source) + 1)
        }
    }
}