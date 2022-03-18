package com.futo.circles.extensions

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import com.futo.circles.core.ErrorParser
import com.futo.circles.core.HasLoadingState
import com.futo.circles.core.SingleEventLiveData

fun <T> SingleEventLiveData<Response<T>>.observeResponse(
    hasLoadingState: HasLoadingState,
    success: (T) -> Unit = {},
    error: ((String) -> Unit)? = null,
    onRequestInvoked: (() -> Unit)? = null
) {
    val owner = hasLoadingState.fragment.viewLifecycleOwner
    observe(owner) {
        it ?: return@observe
        onRequestInvoked?.invoke() ?: run { hasLoadingState.stopLoading() }
        when (it) {
            is Response.Success -> success(it.data)
            is Response.Error -> error?.invoke(it.message)
                ?: hasLoadingState.fragment.showError(it.message)
        }
    }
}

fun <T> LiveData<T>.observeData(fragment: Fragment, observer: (T) -> Unit) {
    val owner = fragment.viewLifecycleOwner
    observe(owner) {
        if (it != null) observer(it)
    }
}

suspend fun <T> createResult(block: suspend () -> T): Response<T> {
    return try {
        Response.Success(block())
    } catch (t: Throwable) {
        Response.Error(ErrorParser.getErrorMessage(t))
    }
}

sealed class Response<out T> {

    data class Success<out T>(val data: T) : Response<T>()

    data class Error(val message: String) : Response<Nothing>()
}