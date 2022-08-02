package org.futo.circles.subscriptions

import com.android.billingclient.api.BillingClient


@Suppress("unused")
sealed class BillingResult<out T> {
    data class Success<out T>(val data: T) : BillingResult<T>()

    data class Failure(@BillingClient.BillingResponse val code: Int) : BillingResult<Nothing>()
    object FeatureNotSupported : BillingResult<Nothing>()
    object NotConnected : BillingResult<Nothing>()
}

@Suppress("unused")
fun <T> BillingResult<T>.unwrap(): T? = (this as? BillingResult.Success)?.data