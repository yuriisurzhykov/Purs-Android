package com.github.yuriisurzhykov.purs.data.cache

interface GetLocationId {

    fun locationId(): Long

    class Const(
        private val constLocationId: Long
    ): GetLocationId {
        override fun locationId(): Long = constLocationId
    }
}