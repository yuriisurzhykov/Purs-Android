package com.github.yuriisurzhykov.purs.data.cache

import javax.inject.Inject

interface GetLocationId {

    fun locationId(): Long

    class Const @Inject constructor(
        private val constLocationId: Long
    ) : GetLocationId {
        override fun locationId(): Long = constLocationId
    }
}