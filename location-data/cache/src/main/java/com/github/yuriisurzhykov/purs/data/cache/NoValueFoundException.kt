package com.github.yuriisurzhykov.purs.data.cache

data class NoValueFoundException(override val message: String) : RuntimeException()