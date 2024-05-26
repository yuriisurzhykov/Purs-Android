package com.github.yuriisurzhykov.purs.core.data

interface HandleError {

    fun handle(exception: Exception): Exception
}