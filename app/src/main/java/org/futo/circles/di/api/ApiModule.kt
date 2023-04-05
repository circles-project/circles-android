package org.futo.circles.di.api

import okhttp3.OkHttpClient
import org.futo.circles.io.BugreportApiService
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val apiModule = module {

    single<Converter.Factory> { GsonConverterFactory.create(get()) }

    single {
        Retrofit.Builder()
            .addConverterFactory(get())
            .client(OkHttpClient.Builder().build())
            .build()
    }

    single {
        get<Retrofit>().create(BugreportApiService::class.java)
    }
}