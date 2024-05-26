package com.github.yuriisurzhykov.purs.data.cloud.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The interface for the main cloud model that contains data about the location name and the working
 * hours for the location.
 * */
interface LocationCloud {

    interface Mapper<T : Any> {
        fun map(locationName: String, workingHours: List<WorkingHourCloud>): T
    }

    fun <T : Any> map(mapper: Mapper<T>): T

    @Serializable
    data class Base(
        @SerialName("location_name")
        private val locationName: String,
        @SerialName("hours")
        private val workingHours: List<WorkingHourCloud.Base>
    ) : LocationCloud {
        override fun <T : Any> map(mapper: Mapper<T>): T = mapper.map(locationName, workingHours)
    }
}