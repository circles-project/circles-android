package org.futo.circles.core.fragment

import android.view.View
import androidx.fragment.app.Fragment
import org.futo.circles.extensions.setEnabledViews
import org.futo.circles.view.LoadingButton

interface HasLoadingState {

    val fragment: Fragment

    fun startLoading(button: LoadingButton) {
        currentLoadingButton = button
        currentLoadingButton?.setIsLoading(true)
        fragment.setEnabledViews(false, alwaysDisabledViews)
    }

    fun stopLoading() {
        currentLoadingButton?.setIsLoading(false)
        currentLoadingButton = null
        fragment.setEnabledViews(true, alwaysDisabledViews)
    }

    fun setAlwaysDisabledViews(views: List<View>) {
        alwaysDisabledViews = views
    }

    companion object {
        private var currentLoadingButton: LoadingButton? = null
        private var alwaysDisabledViews: List<View> = mutableListOf()
    }
}