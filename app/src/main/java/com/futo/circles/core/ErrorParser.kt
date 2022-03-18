package com.futo.circles.core

import org.json.JSONObject
import org.matrix.android.sdk.api.failure.Failure
import retrofit2.HttpException

object ErrorParser {


    fun getErrorMessage(t: Throwable): String =
        handleErrorBodyException(t) ?: handleServerException(t) ?: DEFAULT_ERROR_MESSAGE


    private fun handleErrorBodyException(t: Throwable): String? =
        (t as? HttpException)?.response()?.errorBody()?.string()?.let {
            try {
                JSONObject(it).getString(AUTH_EXCEPTION_REASON_KEY)
            } catch (e: Exception) {
                null
            }
        }

    private fun handleServerException(t: Throwable): String? =
        (t as? Failure.ServerError)?.error?.message ?: t.message

}