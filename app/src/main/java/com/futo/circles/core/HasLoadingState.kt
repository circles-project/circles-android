package com.futo.circles.core

import androidx.fragment.app.Fragment
import com.futo.circles.extensions.setEnabledViews
import com.futo.circles.view.LoadingButton

interface HasLoadingState {

    val fragment: Fragment

    fun startLoading(button: LoadingButton) {
        currentLoadingButton = button
        currentLoadingButton?.setIsLoading(true)
        fragment.setEnabledViews(false)
    }

    fun stopLoading() {
        currentLoadingButton?.setIsLoading(false)
        currentLoadingButton = null
        fragment.setEnabledViews(true)
    }

    companion object {
        private var currentLoadingButton: LoadingButton? = null
    }
}