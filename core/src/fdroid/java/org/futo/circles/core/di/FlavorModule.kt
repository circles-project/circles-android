package org.futo.circles.core.di

import android.content.Context
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.futo.circles.core.feature.notifications.FcmHelper
import org.futo.circles.core.feature.notifications.GuardServiceStarter
import org.futo.circles.core.feature.notifications.test.task.NotificationTestsProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.notifications.FdroidFcmHelper
import org.futo.circles.core.notifications.FdroidGuardServiceStarter
import org.futo.circles.core.notifications.test.FdroidNotificationTestProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorModule {

    companion object {
        @Provides
        fun provideGuardServiceStarter(
            preferences: PreferencesProvider,
            @ApplicationContext appContext: Context
        ): GuardServiceStarter = FdroidGuardServiceStarter(appContext, preferences)

    }

    @Binds
    abstract fun bindsFcmHelper(fcmHelper: FdroidFcmHelper): FcmHelper

    @Binds
    abstract fun bindsNotificationTestProvider(provider: FdroidNotificationTestProvider): NotificationTestsProvider

}