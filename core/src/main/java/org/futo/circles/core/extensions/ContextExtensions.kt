package org.futo.circles.core.extensions

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.core.content.getSystemService
import org.matrix.android.sdk.api.extensions.orFalse
import org.matrix.android.sdk.api.extensions.tryOrNull

fun Context.isConnectedToWifi(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>() ?: return false
    return connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        .orFalse()
}

fun Context.isConnectedToInternet(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>() ?: return false
    val capabilities =
        connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
    val hasWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI).orFalse()
    val hasMobileData = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR).orFalse()
    return hasWifi || hasMobileData
}

fun Context.setInternetConnectionObserver(onConnectionChanged: (Boolean) -> Unit): ConnectivityManager.NetworkCallback {
    onConnectionChanged(isConnectedToInternet())
    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            onConnectionChanged(true)
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            onConnectionChanged(false)
        }
    }
    val request = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET).build()
    getSystemService<ConnectivityManager>()?.registerNetworkCallback(request, networkCallback)
    return networkCallback
}

fun Context.removeInternetConnectionObserver(callback: ConnectivityManager.NetworkCallback) {
    tryOrNull {
        getSystemService<ConnectivityManager>()?.unregisterNetworkCallback(callback)
    }
}
