package com.futo.circles.io.request

import com.google.gson.annotations.SerializedName

data class ValidateSignUpTokenRequestBody(
    @SerializedName("auth") val auth: ValidateTokenRequestParams
)

data class ValidateTokenRequestParams(
    @SerializedName("type") val type: String,
    @SerializedName("token") val token: String,
    @SerializedName("session") val session: String
)