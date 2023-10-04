package org.futo.circles.core

import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import org.futo.circles.core.extensions.addNoInternetConnectionView

class NoInternetConnectionViewPresenter {

    private var noInternetConnectionView: TextView? = null

    fun register(viewLifecycleOwner: LifecycleOwner, viewGroup: ViewGroup?) {
        NetworkObserver.observe(viewLifecycleOwner) { isConnected ->
            val rootViewGroup = viewGroup ?: return@observe
            if (isConnected) {
                noInternetConnectionView?.let {
                    rootViewGroup.removeView(it)
                    noInternetConnectionView = null
                }
            } else {
                if (noInternetConnectionView != null) return@observe
                rootViewGroup.addNoInternetConnectionView()
                    .also { noInternetConnectionView = it }
            }
        }
    }

    fun unregister() {
        noInternetConnectionView = null
    }

}