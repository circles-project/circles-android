package org.futo.circles.auth.di

import androidx.fragment.app.Fragment
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.futo.circles.auth.subscriptions.GoogleSubscriptionsManager
import org.futo.circles.auth.subscriptions.ItemPurchasedListener
import org.futo.circles.auth.subscriptions.SubscriptionManager
import org.futo.circles.auth.subscriptions.SubscriptionProvider

@Module
@InstallIn(SingletonComponent::class)
object SubscriptionsModule {

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