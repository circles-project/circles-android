package org.futo.circles.core.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.futo.circles.core.update.AppUpdateProvider
import org.futo.circles.core.update.CirclesAppUpdateManager

@Module
@InstallIn(SingletonComponent::class)
object AppUpdateModule {

    @Provides
    fun provideAppUpdateProvider(): AppUpdateProvider {
        return object : AppUpdateProvider {
            override fun getManager(): CirclesAppUpdateManager? = null
        }
    }

}