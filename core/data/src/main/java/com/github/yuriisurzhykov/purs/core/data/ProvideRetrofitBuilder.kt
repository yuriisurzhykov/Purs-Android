package com.github.yuriisurzhykov.purs.core.data

import retrofit2.Retrofit

interface ProvideRetrofitBuilder {

    fun provideRetrofitBuilder(): Retrofit.Builder

    abstract class Abstract(
        private val provideConverterFactory: ProvideConverterFactory,
        private val httpClientBuilder: ProvideOkHttpClientBuilder
    ) : ProvideRetrofitBuilder {
        override fun provideRetrofitBuilder(): Retrofit.Builder = Retrofit.Builder()
            .addConverterFactory(provideConverterFactory.converterFactory())
            .client(httpClientBuilder.provideOkHttpClientBuilder().build())
    }
}