package com.github.yuriisurzhykov.purs.core.data

interface CloudDataSource {

    suspend fun <T> handle(block: suspend () -> T): T

    abstract class Abstract(
        private val handleError: HandleError
    ) : CloudDataSource {
        override suspend fun <T> handle(block: suspend () -> T): T {
            return try {
                block.invoke()
            } catch (error: Exception) {
                throw handleError.handle(error)
            }
        }
    }
}