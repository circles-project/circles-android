package org.futo.circles.core.base

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.core.content.getSystemService
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.matrix.android.sdk.api.extensions.orFalse

object NetworkObserver {

    private val internetConnectionFlow = MutableStateFlow(true)

    fun isConnected() = internetConnectionFlow.value

    fun register(context: Context) {
        internetConnectionFlow.value = isConnectedToInternet(context)
        setInternetConnectionObserver(context) { internetConnectionFlow.value = it }
    }

    fun observe(lifecycleOwner: LifecycleOwner, onConnectionChanged: (Boolean) -> Unit) {
        lifecycleOwner.lifecycleScope.launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                internetConnectionFlow.collectLatest {
                    onConnectionChanged(it)
                }
            }
        }
    }

    fun isConnectedToWifi(context: Context): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>() ?: return false
        return connectivityManager.activeNetwork?.let {
            connectivityManager.getNetworkCapabilities(it)
        }?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI).orFalse()
    }

    private fun setInternetConnectionObserver(
        context: Context,
        onConnectionChanged: (Boolean) -> Unit
    ): ConnectivityManager.NetworkCallback {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                onConnectionChanged(true)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                onConnectionChanged(false)
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                onConnectionChanged(isConnectedToInternet(context))
            }
        }
        context.getSystemService<ConnectivityManager>()
            ?.registerDefaultNetworkCallback(networkCallback)
        return networkCallback
    }

    private fun isConnectedToInternet(context: Context): Boolean {
        val connectivityManager = context.getSystemService<ConnectivityManager>() ?: return true
        val capabilities =
            connectivityManager.activeNetwork?.let { connectivityManager.getNetworkCapabilities(it) }
        val hasWifi = capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI).orFalse()
        val hasMobileData =
            capabilities?.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR).orFalse()
        return hasWifi || hasMobileData
    }

}