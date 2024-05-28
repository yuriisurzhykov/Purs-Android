package com.github.yuriisurzhykov.purs.domain.mapper

import com.github.yuriisurzhykov.purs.core.Mapper
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle
import javax.inject.Inject

interface StringToLocalTimeMapper : Mapper<String, LocalTime> {

    abstract class Abstract @Inject constructor(
        private val timeFormatter: DateTimeFormatter
    ) : StringToLocalTimeMapper {
        override fun map(source: String): LocalTime {
            return LocalTime.parse(source, timeFormatter)
        }
    }

    class Base @Inject constructor() :
        Abstract(DateTimeFormatter.ISO_LOCAL_TIME.withResolverStyle(ResolverStyle.LENIENT))
}