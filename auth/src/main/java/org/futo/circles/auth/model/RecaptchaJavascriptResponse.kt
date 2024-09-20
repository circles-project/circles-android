package org.futo.circles.auth.model

import com.google.gson.annotations.SerializedName


data class RecaptchaJavascriptResponse(
    @SerializedName("action") val action: String? = null,
    @SerializedName("response") val response: String? = null
)