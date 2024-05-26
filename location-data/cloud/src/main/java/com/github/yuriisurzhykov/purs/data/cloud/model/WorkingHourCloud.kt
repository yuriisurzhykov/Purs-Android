package com.github.yuriisurzhykov.purs.data.cloud.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

interface WorkingHourCloud {

    interface Mapper<T> {
        fun map(dayName: String, startTime: String, endTime: String): T
    }

    fun <T> map(mapper: Mapper<T>): T

    @Serializable
    data class Base(
        @SerialName("day_of_week")
        private val dayName: String,
        @SerialName("start_local_time")
        private val startTime: String,
        @SerialName("end_local_time")
        private val endTime: String
    ) : WorkingHourCloud {
        override fun <T> map(mapper: Mapper<T>): T =
            mapper.map(dayName, startTime, endTime)
    }
}