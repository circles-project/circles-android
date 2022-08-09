package org.futo.circles.extensions

import android.content.Context
import androidx.annotation.DimenRes

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

fun Context.disableScreenScale(): Context {
    val overrideConfiguration = resources.configuration.apply {
        fontScale = 1f
        densityDpi = resources.displayMetrics.xdpi.toInt()
    }
    return createConfigurationContext(overrideConfiguration)
}