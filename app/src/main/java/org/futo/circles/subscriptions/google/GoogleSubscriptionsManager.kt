package org.futo.circles.subscriptions.google

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.*
import kotlinx.coroutines.suspendCancellableCoroutine
import org.futo.circles.extensions.onBG
import org.futo.circles.subscriptions.BillingResult
import org.futo.circles.subscriptions.SubscriptionData
import org.futo.circles.subscriptions.SubscriptionManager
import kotlin.coroutines.resume

class GoogleSubscriptionsManager(
    private val activity: AppCompatActivity,
    private val itemPurchasedListener: GoogleItemPurchasedListener
) : SubscriptionManager {

    private val subscriptionsList =
        arrayListOf("subscriptions_names_here!")

    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            purchases?.let {
                if (billingResult.responseCode == OK)
                    itemPurchasedListener.onItemPurchased(purchases)
                else itemPurchasedListener.onPurchaseFailed(billingResult.responseCode)
            }
        }

    private val client = BillingClient.newBuilder(activity)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()


    init {
        activity.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                client.endConnection()
                super.onDestroy(owner)
            }
        })
    }

    override suspend fun getDetails() = when (val code =
        client.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode) {

        OK -> queryDetails()
        FEATURE_NOT_SUPPORTED -> BillingResult.FeatureNotSupported
        SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> onBG {
            tryConnectAndDo { queryDetails() }
        }
        DEVELOPER_ERROR -> BillingResult.Failure(DEVELOPER_ERROR)
        else -> BillingResult.Failure(code = code)
    }


    override suspend fun purchaseProduct(productDetails: ProductDetails): BillingResult<String> =
        when (val code = client
            .isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode) {
            OK -> purchase(productDetails)
            FEATURE_NOT_SUPPORTED -> BillingResult.FeatureNotSupported
            SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> onBG {
                tryConnectAndDo { purchase(productDetails) }
            }
            ITEM_ALREADY_OWNED -> BillingResult.Success("Already purchased")
            ITEM_UNAVAILABLE, USER_CANCELED, ITEM_NOT_OWNED -> BillingResult.Failure(
                ITEM_UNAVAILABLE
            )
            DEVELOPER_ERROR -> BillingResult.Failure(DEVELOPER_ERROR)
            ERROR -> BillingResult.Failure(ERROR)
            else -> BillingResult.Failure(code = code)
        }


    private suspend inline fun <T> tryConnectAndDo(action: () -> BillingResult<T>): BillingResult<T> =
        when (val connectResult = client.tryConnect()) {
            is BillingResult.Success -> if (connectResult.data) action() else BillingResult.NotConnected
            is BillingResult.Failure -> connectResult
            is BillingResult.FeatureNotSupported -> connectResult
            is BillingResult.NotConnected -> connectResult
        }


    private suspend fun BillingClient.tryConnect(): BillingResult<Boolean> =
        suspendCancellableCoroutine { continuation ->
            startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: com.android.billingclient.api.BillingResult) {
                    if (continuation.isCancelled || continuation.isCompleted) return
                    continuation.resume(
                        when (billingResult.responseCode) {
                            OK -> BillingResult.Success(true)
                            SERVICE_DISCONNECTED -> BillingResult.NotConnected
                            else -> BillingResult.Failure(billingResult.responseCode)
                        }
                    )
                }

                override fun onBillingServiceDisconnected() {
                    if (continuation.isCancelled || continuation.isCompleted) return
                    continuation.resume(BillingResult.NotConnected)
                }
            })
        }

    private suspend fun queryDetails(): BillingResult<List<SubscriptionData>> {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(subscriptionsList.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            })

        val productDetailsResult = onBG { client.queryProductDetails(params.build()) }

        return if (productDetailsResult.billingResult.responseCode == OK) {
            productDetailsResult.productDetailsList?.filter { subscriptionsList.contains(it.productId) }
                ?.map { SubscriptionData(it) }
                ?.takeIf { it.isNotEmpty() }
                ?.let { BillingResult.Success(data = it) }
                ?: BillingResult.Failure(code = ERROR)
        } else {
            BillingResult.Failure(code = productDetailsResult.billingResult.responseCode)
        }
    }

    private fun purchase(productDetails: ProductDetails): BillingResult<String> {
        val productDetailsParamsList = listOf(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .build()
        )

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(productDetailsParamsList)
            .build()

        return when (val code =
            client.launchBillingFlow(activity, billingFlowParams).responseCode) {
            OK -> BillingResult.Success("Started")
            else -> BillingResult.Failure(code = code)
        }
    }
}