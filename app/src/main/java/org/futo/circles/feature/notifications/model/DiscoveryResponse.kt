package org.futo.circles.feature.notifications.model

import com.google.gson.annotations.SerializedName

data class DiscoveryResponse(
    @SerializedName("unifiedpush") val unifiedpush: DiscoveryUnifiedPush = DiscoveryUnifiedPush()
)

data class DiscoveryUnifiedPush(
    @SerializedName("gateway") val gateway: String = ""
)