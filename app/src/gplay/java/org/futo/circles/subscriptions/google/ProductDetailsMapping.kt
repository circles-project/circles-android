package org.futo.circles.subscriptions.google

import android.content.Context
import com.android.billingclient.api.ProductDetails
import org.futo.circles.auth.model.SubscriptionListItem
import org.futo.circles.auth.subscriptions.formatIsoPeriod
import org.futo.circles.core.extensions.Response

fun ProductDetails.toSubscriptionListItem(context: Context): SubscriptionListItem {
    val productOffer =
        subscriptionOfferDetails?.last()?.pricingPhases?.pricingPhaseList?.last()

    return SubscriptionListItem(
        id = productId,
        name = name,
        description = description,
        price = productOffer?.formattedPrice ?: "",
        duration = productOffer?.billingPeriod?.formatIsoPeriod(context) ?: ""
    )
}

fun Response<List<ProductDetails>>.toSubscriptionListItemsResponse(context: Context): Response<List<SubscriptionListItem>> =
    when (val response = this) {
        is Response.Success -> Response.Success(response.data.map {
            it.toSubscriptionListItem(context)
        })

        is Response.Error -> response
    }