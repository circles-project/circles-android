package org.futo.circles.core.feature.blurhash

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.roundToInt

class ThumbHashChannel(var nx: Int, var ny: Int) {
    var dc = 0f
    var ac: FloatArray
    var scale = 0f

    init {
        var n = 0
        for (cy in 0 until ny) {
            var cx = if (cy > 0) 0 else 1
            while (cx * ny < nx * (ny - cy)) {
                n++
                cx++
            }
        }
        ac = FloatArray(n)
    }

    fun encode(w: Int, h: Int, channel: FloatArray): ThumbHashChannel {
        var n = 0
        val fx = FloatArray(w)
        for (cy in 0 until ny) {
            var cx = 0
            while (cx * ny < nx * (ny - cy)) {
                var f = 0f
                for (x in 0 until w) fx[x] = cos(Math.PI / w * cx * (x + 0.5f)).toFloat()
                for (y in 0 until h) {
                    val fy = cos(Math.PI / h * cy * (y + 0.5f)).toFloat()
                    for (x in 0 until w) f += channel[x + y * w] * fx[x] * fy
                }
                f /= (w * h).toFloat()
                if (cx > 0 || cy > 0) {
                    ac[n++] = f
                    scale = max(scale, abs(f))
                } else {
                    dc = f
                }
                cx++
            }
        }
        if (scale > 0) for (i in ac.indices) ac[i] = 0.5f + 0.5f / scale * ac[i]
        return this
    }

    fun decode(hash: ByteArray, start: Int, initialIndex: Int, scale: Float): Int {
        var index = initialIndex
        for (i in ac.indices) {
            val data = hash[start + (index shr 1)].toInt() shr (index and 1 shl 2)
            ac[i] = ((data and 15).toFloat() / 7.5f - 1.0f) * scale
            index++
        }
        return index
    }

    fun writeTo(hash: ByteArray, start: Int, initialIndex: Int): Int {
        var index = initialIndex
        for (v in ac) {
            hash[start + (index shr 1)] =
                (hash[start + (index shr 1)].toInt() or ((15.0f * v).roundToInt() shl (index and 1 shl 2))).toByte()
            index++
        }
        return index
    }
}