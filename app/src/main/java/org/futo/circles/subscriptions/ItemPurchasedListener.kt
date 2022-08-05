package org.futo.circles.subscriptions


interface ItemPurchasedListener {

    fun onItemPurchased(purchase: String)

    fun onPurchaseFailed(errorCode: Int)

}