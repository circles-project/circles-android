package org.futo.circles.feature.notifications

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import org.matrix.android.sdk.api.extensions.orFalse

class WifiDetector(context: Context) {
    private val connectivityManager = context.getSystemService<ConnectivityManager>()

    fun isConnectedToWifi(): Boolean = connectivityManager?.activeNetwork
        ?.let { connectivityManager.getNetworkCapabilities(it) }
        ?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        .orFalse()
}
