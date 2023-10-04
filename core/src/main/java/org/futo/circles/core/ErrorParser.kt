package org.futo.circles.core

import org.json.JSONObject
import org.matrix.android.sdk.api.failure.Failure
import org.matrix.android.sdk.api.session.crypto.MXCryptoError
import retrofit2.HttpException

object ErrorParser {

    private const val AUTH_EXCEPTION_REASON_KEY = "reason"

    fun getErrorMessage(t: Throwable): String =
        handleErrorBodyException(t) ?: handleOtherException(t) ?: "Unexpected error"


    private fun handleErrorBodyException(t: Throwable): String? =
        (t as? HttpException)?.response()?.errorBody()?.string()?.let {
            try {
                JSONObject(it).getString(AUTH_EXCEPTION_REASON_KEY)
            } catch (e: Exception) {
                null
            }
        }

    private fun handleOtherException(t: Throwable): String? = when (t) {
        is Failure.NetworkConnection -> "No network. Please check your Internet connection"
        is Failure.ServerError -> t.error.message
        is MXCryptoError.Base -> t.technicalMessage
        else -> t.message
    }

}