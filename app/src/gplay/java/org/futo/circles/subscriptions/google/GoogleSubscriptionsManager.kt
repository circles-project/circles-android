package org.futo.circles.subscriptions.google

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.android.billingclient.api.*
import com.android.billingclient.api.BillingClient.BillingResponseCode.*
import kotlinx.coroutines.suspendCancellableCoroutine
import org.futo.circles.R
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.onBG
import org.futo.circles.model.SubscriptionListItem
import org.futo.circles.subscriptions.ItemPurchasedListener
import org.futo.circles.subscriptions.SubscriptionManager
import kotlin.coroutines.resume

class GoogleSubscriptionsManager(
    private val activity: Activity,
    private val itemPurchasedListener: ItemPurchasedListener
) : SubscriptionManager {


    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            purchases?.let {
                if (billingResult.responseCode == OK)
                    itemPurchasedListener.onItemPurchased(
                        purchases.lastOrNull()?.originalJson ?: ""
                    )
                else itemPurchasedListener.onPurchaseFailed(billingResult.responseCode)
            }
        }

    private val client = BillingClient.newBuilder(activity)
        .setListener(purchasesUpdatedListener)
        .enablePendingPurchases()
        .build()


    init {
        (activity as? AppCompatActivity)?.lifecycle?.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                client.endConnection()
                super.onDestroy(owner)
            }
        })
    }

    override suspend fun getDetails(productIds: List<String>): Response<List<SubscriptionListItem>> =
        when (val code =
            client.isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode) {
            OK -> queryDetails(productIds).toSubscriptionListItemsResponse(activity.applicationContext)
            SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> onBG {
                tryConnectAndDo { queryDetails(productIds).toSubscriptionListItemsResponse(activity.applicationContext) }
            }
            else -> getErrorResponseForCode(code)
        }


    override suspend fun purchaseProduct(productId: String): Response<Unit> {
        val detailsResponse = queryDetails(listOf(productId))
        val productDetails =
            (detailsResponse as? Response.Success)?.data?.firstOrNull { it.productId == productId }
                ?: return getErrorResponseForCode(ERROR)

        return when (val code = client
            .isFeatureSupported(BillingClient.FeatureType.SUBSCRIPTIONS).responseCode) {
            OK -> purchase(productDetails)
            SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> onBG {
                tryConnectAndDo { purchase(productDetails) }
            }
            ITEM_ALREADY_OWNED -> Response.Success(Unit)
            else -> getErrorResponseForCode(code)
        }
    }

    private suspend inline fun <T> tryConnectAndDo(action: () -> Response<T>): Response<T> =
        when (val connectResult = client.tryConnect()) {
            is Response.Success -> if (connectResult.data) action()
            else getErrorResponseForCode(SERVICE_DISCONNECTED)
            is Response.Error -> connectResult
        }


    private suspend fun BillingClient.tryConnect(): Response<Boolean> =
        suspendCancellableCoroutine { continuation ->
            startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: com.android.billingclient.api.BillingResult) {
                    if (continuation.isCancelled || continuation.isCompleted) return
                    continuation.resume(
                        when (billingResult.responseCode) {
                            OK -> Response.Success(true)
                            else -> getErrorResponseForCode(billingResult.responseCode)
                        }
                    )
                }

                override fun onBillingServiceDisconnected() {
                    if (continuation.isCancelled || continuation.isCompleted) return
                    continuation.resume(getErrorResponseForCode(SERVICE_DISCONNECTED))
                }
            })
        }

    private suspend fun queryDetails(productIds: List<String>): Response<List<ProductDetails>> {
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productIds.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            })

        val productDetailsResult = onBG { client.queryProductDetails(params.build()) }

        return if (productDetailsResult.billingResult.responseCode == OK) {
            productDetailsResult.productDetailsList?.filter { productIds.contains(it.productId) }
                ?.takeIf { it.isNotEmpty() }
                ?.let { Response.Success(data = it) }
                ?: getErrorResponseForCode(code = ERROR)
        } else {
            getErrorResponseForCode(productDetailsResult.billingResult.responseCode)
        }
    }

    private fun purchase(productDetails: ProductDetails): Response<Unit> {
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
            OK -> Response.Success(Unit)
            else -> getErrorResponseForCode(code)
        }
    }

    private fun getErrorResponseForCode(code: Int) = when (code) {
        FEATURE_NOT_SUPPORTED -> Response.Error(activity.getString(R.string.feature_not_supported))
        SERVICE_DISCONNECTED, SERVICE_UNAVAILABLE, BILLING_UNAVAILABLE -> Response.Error(activity.getString(R.string.service_unavailable))
        ITEM_UNAVAILABLE -> Response.Error(activity.getString(R.string.item_unavailable))
        USER_CANCELED -> Response.Error(activity.getString(R.string.user_canceled))
        ITEM_NOT_OWNED -> Response.Error(activity.getString(R.string.item_not_owned))
        DEVELOPER_ERROR -> Response.Error(activity.getString(R.string.developer_error))
        else -> Response.Error(activity.getString(R.string.purchase_failed_format, code))
    }
}