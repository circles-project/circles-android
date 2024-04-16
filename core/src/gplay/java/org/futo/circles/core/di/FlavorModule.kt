package org.futo.circles.core.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.futo.circles.core.feature.notifications.FcmHelper
import org.futo.circles.core.feature.notifications.GuardServiceStarter
import org.futo.circles.core.feature.notifications.test.task.NotificationTestsProvider
import org.futo.circles.core.notifications.GoogleFcmHelper
import org.futo.circles.core.notifications.test.GoogleNotificationTestProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorModule {

    companion object {
        @Provides
        fun provideGuardServiceStarter(): GuardServiceStarter {
            return object : GuardServiceStarter {}
        }
    }

    @Binds
    abstract fun bindsFcmHelper(fcmHelper: GoogleFcmHelper): FcmHelper

    @Binds
    abstract fun bindsNotificationTestProvider(provider: GoogleNotificationTestProvider): NotificationTestsProvider

}