package org.futo.circles.core.utils

import android.util.Size

object MediaUtils {

    fun getSizeBasedOnOrientation(orientation: Int, width: Int, height: Int): Size {
        return if (orientation == 90 || orientation == 270) Size(height, width)
        else Size(width, height)
    }

    fun getThumbSizeWithLimits(viewWidth: Int, originalSize: Size): Size {
        val aspectRatio = originalSize.width.toFloat() / originalSize.height.toFloat()

        val maxHeight = (1.35 * viewWidth).toInt()
        val minHeight = (viewWidth * 0.25).toInt()

        val aspectHeight = (viewWidth / aspectRatio).toInt()

        val height = if (aspectHeight in minHeight..maxHeight) aspectHeight
        else if (aspectHeight < minHeight) minHeight else maxHeight

        return Size(viewWidth, height)
    }

}