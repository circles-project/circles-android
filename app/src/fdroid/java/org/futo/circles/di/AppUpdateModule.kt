package org.futo.circles.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.futo.circles.update.AppUpdateManager
import org.futo.circles.update.AppUpdateProvider

@Module
@InstallIn(SingletonComponent::class)
object AppUpdateModule {

    @Provides
    fun provideAppUpdateProvider(): AppUpdateProvider {
        return object : AppUpdateProvider {
            override fun getManager(): AppUpdateManager? = null
        }
    }

}