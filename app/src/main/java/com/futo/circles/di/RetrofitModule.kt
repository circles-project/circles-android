package com.futo.circles.di

import com.futo.circles.BuildConfig
import com.futo.circles.io.service.CirclesSignUpService
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val retrofitModule = module {
    single {
        Retrofit.Builder()
            .baseUrl(BuildConfig.MATRIX_HOME_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single {
        get<Retrofit>().create(CirclesSignUpService::class.java)
    }

}