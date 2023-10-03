package org.futo.circles.core

import android.app.Activity
import android.net.ConnectivityManager
import android.view.ViewGroup
import android.widget.TextView
import org.futo.circles.core.extensions.addNoInternetConnectionView
import org.futo.circles.core.extensions.removeInternetConnectionObserver
import org.futo.circles.core.extensions.setInternetConnectionObserver

class NoInternetConnectionViewPresenter {

    private var noInternetConnectionView: TextView? = null
    private var internetConnectionObserver: ConnectivityManager.NetworkCallback? = null

    fun register(activity: Activity, viewGroup: ViewGroup?) {
        activity.setInternetConnectionObserver { isConnected ->
            val rootViewGroup = viewGroup ?: return@setInternetConnectionObserver
            activity.runOnUiThread {
                if (isConnected) {
                    noInternetConnectionView?.let {
                        rootViewGroup.removeView(it)
                        noInternetConnectionView = null
                    }
                } else {
                    if (noInternetConnectionView != null) return@runOnUiThread
                    rootViewGroup.addNoInternetConnectionView()
                        .also { noInternetConnectionView = it }
                }
            }
        }
    }

    fun unregister(activity: Activity) {
        internetConnectionObserver?.let { activity.removeInternetConnectionObserver(it) }
        noInternetConnectionView = null
    }

}