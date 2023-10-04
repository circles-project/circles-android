package org.futo.circles.core.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import org.matrix.android.sdk.api.extensions.orFalse

fun Context.isConnectedToWifi(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>() ?: return false
    return connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        .orFalse()
}
