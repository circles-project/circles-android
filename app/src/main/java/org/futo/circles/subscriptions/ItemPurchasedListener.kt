package org.futo.circles.subscriptions


interface ItemPurchasedListener {

    fun onItemPurchased(productId: String, purchaseToken: String)

    fun onPurchaseFailed(errorCode: Int)

}