package org.futo.circles.feature.settings.active_sessions.bootstrap

import androidx.annotation.StringRes

interface BootstrapProgressListener {
    fun onProgress(@StringRes messageResId: Int)
}
