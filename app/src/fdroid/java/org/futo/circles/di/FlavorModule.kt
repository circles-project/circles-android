package org.futo.circles.di

import android.content.Context
import androidx.fragment.app.Fragment
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import org.futo.circles.R
import org.futo.circles.auth.subscriptions.ItemPurchasedListener
import org.futo.circles.auth.subscriptions.SubscriptionManager
import org.futo.circles.auth.subscriptions.SubscriptionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.feature.notifications.GuardServiceStarter
import org.futo.circles.feature.notifications.test.task.NotificationTestsProvider
import org.futo.circles.notifications.FdroidFcmHelper
import org.futo.circles.notifications.FdroidGuardServiceStarter
import org.futo.circles.notifications.test.FdroidNotificationTestProvider

@Module
@InstallIn(SingletonComponent::class)
abstract class FlavorModule {

    companion object {
        @Provides
        fun provideGuardServiceStarter(
            preferences: PreferencesProvider,
            @ApplicationContext appContext: Context
        ): GuardServiceStarter = FdroidGuardServiceStarter(appContext, preferences)

        @Provides
        fun provideSubscriptionProvider(): SubscriptionProvider {
            return object : SubscriptionProvider {
                override fun getManager(
                    fragment: Fragment,
                    itemPurchaseListener: ItemPurchasedListener?
                ): SubscriptionManager =
                    throw IllegalStateException(fragment.getString(R.string.flavour_does_not_support_subscriptions))
            }
        }
    }

    @Binds
    abstract fun bindsFcmHelper(fcmHelper: FdroidFcmHelper): FcmHelper

    @Binds
    abstract fun bindsNotificationTestProvider(provider: FdroidNotificationTestProvider): NotificationTestsProvider

}