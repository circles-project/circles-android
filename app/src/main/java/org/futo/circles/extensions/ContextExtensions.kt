package org.futo.circles.extensions

import android.content.Context
import androidx.annotation.DimenRes
import org.futo.circles.R

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)

fun Context.disableScreenScale(): Context {
    val overrideConfiguration = resources.configuration.apply {
        fontScale = 1f
        densityDpi = resources.displayMetrics.xdpi.toInt()
    }
    return createConfigurationContext(overrideConfiguration)
}

fun Context.initialDisplayName() =
    getString(R.string.initial_device_name, getString(R.string.full_app_name))