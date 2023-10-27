package org.futo.circles.core.feature.textDrawable

import kotlin.math.abs

class ColorGenerator {

    private val colors = mutableListOf(
        -0xe9c9c,
        -0xa7aa7,
        -0x65bc2,
        -0x1b39d2,
        -0x98408c,
        -0xa65d42,
        -0xdf6c33,
        -0x529d59,
        -0x7fa87f
    )

    fun getColor(key: Any): Int {
        return colors[abs(key.hashCode()) % colors.size]
    }
}