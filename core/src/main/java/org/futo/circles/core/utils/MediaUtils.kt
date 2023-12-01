package org.futo.circles.core.utils

import android.util.Size

object MediaUtils {

    fun getSizeBasedOnOrientation(orientation: Int, width: Int, height: Int): Size {
        return if (orientation == 90 || orientation == 270) Size(height, width)
        else Size(width, height)
    }

}