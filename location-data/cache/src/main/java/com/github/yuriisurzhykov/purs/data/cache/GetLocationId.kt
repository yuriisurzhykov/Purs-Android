package com.github.yuriisurzhykov.purs.data.cache

import javax.inject.Inject

/**
 * An interface to provide a location id to persist data. The logic to provide the location if
 * moved to this interface because currently we have only one location at the moment, but if
 * it will be decided to show more than one location, the new logic will be implemented.
 * */
interface GetLocationId {

    fun locationId(): Long

    class Const @Inject constructor(
        private val constLocationId: Long
    ) : GetLocationId {
        override fun locationId(): Long = constLocationId
    }
}