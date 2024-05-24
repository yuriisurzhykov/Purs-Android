package com.github.yuriisurzhykov.purs.core

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

interface Dispatchers {

    fun launchUi(scope: CoroutineScope, block: suspend CoroutineScope.() -> Unit): Job
    fun launchBackground(scope: CoroutineScope, block: suspend CoroutineScope.() -> Unit): Job
    suspend fun switchToUi(block: suspend CoroutineScope.() -> Unit)

    abstract class Abstract(
        private val uiScope: CoroutineDispatcher,
        private val backgroundScope: CoroutineDispatcher
    ) : Dispatchers {
        override fun launchUi(
            scope: CoroutineScope,
            block: suspend CoroutineScope.() -> Unit
        ): Job = scope.launch(uiScope, block = block)

        override fun launchBackground(
            scope: CoroutineScope,
            block: suspend CoroutineScope.() -> Unit
        ): Job = scope.launch(backgroundScope, block = block)

        override suspend fun switchToUi(block: suspend CoroutineScope.() -> Unit) =
            withContext(uiScope, block)
    }

    class Base : Abstract(
        uiScope = kotlinx.coroutines.Dispatchers.Main,
        backgroundScope = kotlinx.coroutines.Dispatchers.Default
    )
}
