package org.futo.circles.auth.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.futo.circles.auth.credentials.CredentialsManager
import org.futo.circles.auth.credentials.CredentialsProvider
import org.futo.circles.auth.credentials.GoogleCredentialsManager

@Module
@InstallIn(SingletonComponent::class)
object CredentialsModule {

    @Provides
    fun provideCredentialsProvider(): CredentialsProvider {
        return object : CredentialsProvider {
            override fun getManager(): CredentialsManager = GoogleCredentialsManager()
        }
    }

}