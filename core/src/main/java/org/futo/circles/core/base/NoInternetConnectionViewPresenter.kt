package org.futo.circles.core.base

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
                noInternetConnectionView = rootViewGroup.addNoInternetConnectionView()
            }
        }
    }

    fun unregister() {
        noInternetConnectionView = null
    }

}