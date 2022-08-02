package org.futo.circles.subscriptions.google

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.*
import org.futo.circles.subscriptions.SubscriptionData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.futo.circles.subscriptions.BillingResult
import org.futo.circles.subscriptions.SubscriptionManager
import kotlin.coroutines.resume

class GoogleSubscriptionsManager(private val activity: AppCompatActivity) : SubscriptionManager, LifecycleObserver {

    private val subscriptionsList =
            listOf("subscriptions_names_here!")

    private var purchaseListener: GoogleItemPurchasedListener? = null

    private val client = BillingClient.newBuilder(activity)
            .setListener { responseCode, purchases ->
                purchases?.let {
                    purchaseListener?.let {
                        if (responseCode == OK) it.onItemPurchased(purchases)
                        else it.onPurchaseFailed(responseCode)
                    }
                }
            }
            .build()

    init {
        activity.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun destroy() = client.endConnection()

    override suspend fun getDetails(): BillingResult<List<SubscriptionData>> {
        suspend fun queryPremium(): BillingResult<List<SubscriptionData>> =
                suspendCancellableCoroutine { continuation ->
                    val params = SkuDetailsParams.newBuilder()
                            .setSkusList(subscriptionsList)
                            .setType(BillingClient.SkuType.SUBS)
                            .build()

                    client.querySkuDetailsAsync(params) query@{ responseCode, skuDetailsList ->
                        if (continuation.isCancelled) return@query

                        val result = if (responseCode != OK) {
                            BillingResult.Failure(code = responseCode)
                        } else {
                            skuDetailsList
                                    ?.filter { subscriptionsList.contains(it.sku) }
                                    ?.map {
                                        SubscriptionData(
                                                sku = it.sku,
                                                price = it.price,
                                                duration = it.subscriptionPeriod
                                        )
                                    }
                                    ?.takeIf { it.isNotEmpty() }
                                    ?.let { BillingResult.Success(data = it) }
                                    ?: BillingResult.Failure(code = ERROR)
                        }

                        continuation.resume(result)
                    }
                }


        return when (val code = client
                .isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)) {

            OK -> queryPremium()

            FEATURE_NOT_SUPPORTED -> BillingResult.FeatureNotSupported

            SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> withContext(Dispatchers.IO) {
                tryConnectAndDo { queryPremium() }
            }

            DEVELOPER_ERROR -> BillingResult.Failure(DEVELOPER_ERROR)

            else -> BillingResult.Failure(code = code)
        }

    }

    override suspend fun getLastSubscriptionItem(): BillingResult<String> {
        suspend fun querySubscriptionHistory(): BillingResult<String> =
                suspendCancellableCoroutine { continuation ->

                    client.queryPurchaseHistoryAsync(BillingClient.SkuType.SUBS) query@{ responseCode, purchaseHistoryList ->
                        if (continuation.isCancelled) return@query

                        val result = if (responseCode != OK) {
                            BillingResult.Failure(code = responseCode)
                        } else purchaseHistoryList
                                ?.find { subscriptionsList.contains(it.sku) }
                                ?.let { premium ->

                                    BillingResult.Success(data = premium.originalJson)
                                } ?: BillingResult.Failure(code = ERROR)

                        continuation.resume(result)
                    }
                }


        return when (val code = client
                .isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)) {

            OK -> querySubscriptionHistory()

            FEATURE_NOT_SUPPORTED -> BillingResult.FeatureNotSupported

            SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> withContext(Dispatchers.IO) {
                tryConnectAndDo { querySubscriptionHistory() }
            }

            DEVELOPER_ERROR -> BillingResult.Failure(DEVELOPER_ERROR)

            else -> BillingResult.Failure(code = code)
        }

    }


    @Suppress("DEPRECATION")
    override suspend fun purchase(sku: String): BillingResult<String> {
        suspend fun purchase(): BillingResult<String> =
                suspendCancellableCoroutine { continuation ->
                    val params = BillingFlowParams.newBuilder()
                            .setSku(sku)
                            .setType(BillingClient.SkuType.SUBS)
                            .build()

                    when (val result = client.launchBillingFlow(activity, params)) {
                        OK -> purchaseListener = object : GoogleItemPurchasedListener {
                            override fun onItemPurchased(purchases: List<Purchase>) {
                                if (continuation.isCancelled) return

                                purchases.find { subscriptionsList.contains(it.sku) }
                                        ?.let { continuation.resume(BillingResult.Success(it.originalJson)) }
                                        ?: run { continuation.resume(BillingResult.Failure(ERROR)) }

                            }

                            override fun onPurchaseFailed(errorCode: Int) {
                                if (continuation.isCancelled) return

                                continuation.resume(BillingResult.Failure(code = errorCode))
                            }
                        }

                        else -> continuation.resume(BillingResult.Failure(code = result))

                    }
                }


        return when (val code = client
                .isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS)) {

            OK -> purchase()

            FEATURE_NOT_SUPPORTED -> BillingResult.FeatureNotSupported

            SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> withContext(Dispatchers.IO) {
                tryConnectAndDo { purchase() }
            }

            ITEM_ALREADY_OWNED -> BillingResult.Success("")

            ITEM_UNAVAILABLE, USER_CANCELED, ITEM_NOT_OWNED -> BillingResult.Failure(ITEM_UNAVAILABLE)

            DEVELOPER_ERROR -> BillingResult.Failure(DEVELOPER_ERROR)

            ERROR -> BillingResult.Failure(ERROR)

            else -> BillingResult.Failure(code = code)
        }

    }


    private suspend inline fun <T> tryConnectAndDo(action: () -> BillingResult<T>): BillingResult<T> =
            when (val connectResult = client.tryConnect()) {
                is BillingResult.Success -> if (connectResult.data) action() else BillingResult.NotConnected
                is BillingResult.Failure -> connectResult
                is BillingResult.FeatureNotSupported -> connectResult
                is BillingResult.NotConnected -> connectResult
            }

    @BillingResponse
    private suspend fun BillingClient.tryConnect(): BillingResult<Boolean> =
            suspendCancellableCoroutine { continuation ->
                startConnection(object : BillingClientStateListener {
                    override fun onBillingSetupFinished(responseCode: Int) {
                        if (continuation.isCancelled || continuation.isCompleted) return

                        continuation.resume(when (responseCode) {
                            OK -> BillingResult.Success(true)
                            SERVICE_DISCONNECTED -> BillingResult.NotConnected
                            else -> BillingResult.Failure(responseCode)
                        })
                    }

                    override fun onBillingServiceDisconnected() {
                        if (continuation.isCancelled || continuation.isCompleted) return

                        continuation.resume(BillingResult.NotConnected)
                    }
                })
            }
}