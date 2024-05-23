package com.github.yuriisurzhykov.purs.core

interface Mapper<S, R> {

    fun map(source: S): R

    interface Unit<T> : Mapper<T, Unit<T>>
}