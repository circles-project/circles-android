package org.futo.circles.core.base.fragment

import android.view.View
import androidx.fragment.app.Fragment
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.view.LoadingButton

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