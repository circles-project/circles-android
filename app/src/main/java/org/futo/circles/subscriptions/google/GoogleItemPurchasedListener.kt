package org.futo.circles.subscriptions.google

import com.android.billingclient.api.Purchase


interface GoogleItemPurchasedListener {

    fun onItemPurchased(purchases: List<Purchase>)

    fun onPurchaseFailed(errorCode: Int)

}