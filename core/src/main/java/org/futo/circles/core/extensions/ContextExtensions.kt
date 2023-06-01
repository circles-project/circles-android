package org.futo.circles.core.extensions

import android.content.Context

fun Context.disableScreenScale(): Context {
    val overrideConfiguration = resources.configuration.apply {
        fontScale = 1f
        densityDpi = resources.displayMetrics.xdpi.toInt()
    }
    return createConfigurationContext(overrideConfiguration)
}