package com.github.yuriisurzhykov.purs.core.data

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

interface ProvideOkHttpClientBuilder {

    fun provideOkHttpClientBuilder(): OkHttpClient.Builder

    abstract class Abstract(
        private val provideInterceptor: ProvideInterceptor,
        private val timeoutSeconds: Long = 60L
    ) : ProvideOkHttpClientBuilder {
        override fun provideOkHttpClientBuilder() = OkHttpClient.Builder()
            .addInterceptor(provideInterceptor.interceptor())
            .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
    }

    class Base(provideInterceptor: ProvideInterceptor) : Abstract(provideInterceptor)
}
