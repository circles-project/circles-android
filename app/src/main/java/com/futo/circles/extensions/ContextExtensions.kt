package com.futo.circles.extensions

import android.content.Context
import android.support.annotation.DimenRes

fun Context.dimen(@DimenRes resource: Int): Int = resources.getDimensionPixelSize(resource)