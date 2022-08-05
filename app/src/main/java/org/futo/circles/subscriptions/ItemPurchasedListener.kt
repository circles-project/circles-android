package org.futo.circles.subscriptions.google


interface GoogleItemPurchasedListener {

    fun onItemPurchased(purchase: String)

    fun onPurchaseFailed(errorCode: Int)

}