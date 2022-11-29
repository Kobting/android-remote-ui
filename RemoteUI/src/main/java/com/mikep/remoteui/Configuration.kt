package com.mikep.remoteui

import com.github.kobting.remoteui.serialization.viewSerializersModule_v1
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.mikep.remoteui.repository.RemoteUIRepository
import com.mikep.remoteui.repository.RemoteUIService
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.plus
import okhttp3.MediaType
import retrofit2.Retrofit

val json = Json {
    encodeDefaults = true
    isLenient = true
    ignoreUnknownKeys = true
    allowSpecialFloatingPointValues = true
    allowStructuredMapKeys = true
    prettyPrint = true
    useArrayPolymorphism = false //Needs to be false. Default is true
    serializersModule += viewSerializersModule_v1
}

var remoteUIBaseUrl = "http://192.168.50.90:8080/"
    set(value) {
        field = value
        remoteUIRepository = RemoteUIRepository(
            Retrofit.Builder()
                .baseUrl(value)
                .addConverterFactory(json.asConverterFactory(MediaType.parse("application/json")!!))
                .build().create(RemoteUIService::class.java))
    }

var remoteUIRepository: RemoteUIRepository = RemoteUIRepository(
    Retrofit.Builder()
    .baseUrl(remoteUIBaseUrl)
    .addConverterFactory(json.asConverterFactory(MediaType.parse("application/json")!!))
    .build().create(RemoteUIService::class.java))