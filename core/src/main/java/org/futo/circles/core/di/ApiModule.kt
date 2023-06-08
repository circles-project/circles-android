package org.futo.circles.core.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import org.futo.circles.core.getRageShakeUrl
import org.futo.circles.core.rageshake.io.BugreportApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Singleton
    @Provides
    fun provideGson() = GsonBuilder().create()

    @Singleton
    @Provides
    fun provideGsonConverterFactory(gson: Gson): GsonConverterFactory =
        GsonConverterFactory.create(gson)

    @Singleton
    @Provides
    fun provideRetrofit(converterFactory: GsonConverterFactory): Retrofit = Retrofit.Builder()
        .baseUrl(getRageShakeUrl())
        .addConverterFactory(converterFactory)
        .client(OkHttpClient.Builder().build())
        .build()

    @Singleton
    @Provides
    fun provideBugreportApiService(retrofit: Retrofit): BugreportApiService = retrofit.create(
        BugreportApiService::class.java
    )

}