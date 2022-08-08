package org.futo.circles.subscriptions.google

import android.content.Context
import com.android.billingclient.api.ProductDetails
import org.futo.circles.extensions.Response
import org.futo.circles.model.SubscriptionListItem
import org.futo.circles.subscriptions.formatIsoPeriod

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