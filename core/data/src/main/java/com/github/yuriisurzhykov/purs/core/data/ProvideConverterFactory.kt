package com.github.yuriisurzhykov.purs.core.data

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Converter

interface ProvideConverterFactory {

    fun converterFactory(): Converter.Factory

    abstract class Abstract(
        private val mediaType: MediaType = "application/json; charset=UTF8".toMediaType()
    ) : ProvideConverterFactory {
        override fun converterFactory(): Converter.Factory {
            return Json.asConverterFactory(mediaType)
        }
    }
}
