package org.futo.circles.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.notifications.FdroidFcmHelper
import org.futo.circles.notifications.FdroidGuardServiceStarter

@InstallIn(SingletonComponent::class)
@Module
abstract class FlavorModule {

    companion object {
        @Provides
        fun provideGuardServiceStarter(
            preferences: PreferencesProvider,
            appContext: Context
        ): GuardServiceStarter {
            return FdroidGuardServiceStarter(appContext, preferences)
        }
    }

    @Binds
    abstract fun bindsFcmHelper(fcmHelper: FdroidFcmHelper): FcmHelper

}