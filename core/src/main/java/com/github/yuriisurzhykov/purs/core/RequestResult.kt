package com.github.yuriisurzhykov.purs.core

sealed class RequestResult<out E : Any>(open val data: E? = null) {
    data class InProgress<E : Any>(override val data: E? = null) : RequestResult<E>(data)

    data class Success<E : Any>(override val data: E) : RequestResult<E>(data)

    data class Error<E : Any>(override val data: E? = null, val error: Throwable? = null) :
        RequestResult<E>(data)
}

fun <I : Any, O : Any> RequestResult<I>.map(mapper: (I) -> O): RequestResult<O> {
    return when (this) {
        is RequestResult.Success -> RequestResult.Success(mapper(data))
        is RequestResult.Error -> RequestResult.Error(data?.let(mapper))
        is RequestResult.InProgress -> RequestResult.InProgress(data?.let(mapper))
    }
}

internal fun <T : Any> Result<T>.toRequestResult(): RequestResult<T> {
    return when {
        isSuccess -> RequestResult.Success(getOrThrow())
        isFailure -> RequestResult.Error(data = null, error = exceptionOrNull())
        else -> error("Impossible branch")
    }
}