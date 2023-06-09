package org.futo.circles.di

import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.futo.circles.auth.subscriptions.ItemPurchasedListener
import org.futo.circles.auth.subscriptions.SubscriptionManager
import org.futo.circles.auth.subscriptions.SubscriptionProvider
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.test.task.NotificationTestsProvider
import org.futo.circles.notifications.GoogleFcmHelper
import org.futo.circles.notifications.test.GoogleNotificationTestProvider
import org.futo.circles.subscriptions.google.GoogleSubscriptionsManager

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorModule {

    companion object {
        @Provides
        fun provideGuardServiceStarter(): GuardServiceStarter {
            return object : GuardServiceStarter {}
        }

        @Provides
        fun provideSubscriptionProvider(): SubscriptionProvider {
            return object : SubscriptionProvider {
                override fun getManager(
                    fragment: Fragment,
                    itemPurchaseListener: ItemPurchasedListener?
                ): SubscriptionManager = GoogleSubscriptionsManager(fragment, itemPurchaseListener)
            }
        }
    }

    @Binds
    abstract fun bindsFcmHelper(fcmHelper: GoogleFcmHelper): FcmHelper

    @Binds
    abstract fun bindsNotificationTestProvider(provider: GoogleNotificationTestProvider): NotificationTestsProvider

}