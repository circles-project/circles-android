package org.futo.circles.di.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import org.futo.circles.base.getRageShakeUrl
import org.futo.circles.io.BugreportApiService
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {

    single<Gson> { GsonBuilder().create() }

    single<Converter.Factory> { GsonConverterFactory.create(get()) }

    single {
        Retrofit.Builder()
            .baseUrl(getRageShakeUrl())
            .addConverterFactory(get())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    single {
        get<Retrofit>().create(BugreportApiService::class.java)
    }
}