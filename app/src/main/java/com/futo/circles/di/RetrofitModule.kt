package com.futo.circles.di

import com.futo.circles.BuildConfig
import com.futo.circles.feature.log_in.data_source.LoginDataSource
import com.futo.circles.io.service.CirclesSignUpService
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit

val retrofitModule = module {
    single{
        Retrofit.Builder()
            .baseUrl(BuildConfig.MATRIX_HOME_SERVER_URL)
            .addConverterFactory(get())
            .build()
    }

    single {
        get<Retrofit>().create(CirclesSignUpService::class.java)
    }

}