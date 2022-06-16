package org.futo.circles.extensions

import android.content.Context
import androidx.annotation.DimenRes

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)