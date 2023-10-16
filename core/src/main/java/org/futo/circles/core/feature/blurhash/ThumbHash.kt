package org.futo.circles.core.feature.blurhash

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.ByteBuffer
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

object ThumbHash {

    private const val MAX_WIDTH = 100
    private const val MAX_HEIGHT = 100

    suspend fun getThumbHash(context: Context, uri: Uri): String {
        val initialBitmap = withContext(Dispatchers.IO) {
            Glide.with(context)
                .asBitmap()
                .load(uri)
                .fitCenter()
                .format(DecodeFormat.PREFER_ARGB_8888)
                .override(MAX_WIDTH, MAX_HEIGHT)
                .submit()
                .get()
        }
        val argbBytes = bitmapToRgbaByteArray(initialBitmap)
        val hashBytes = rgbaToThumbHash(initialBitmap.width, initialBitmap.height, argbBytes)
        return Base64.encodeToString(hashBytes, Base64.DEFAULT)
    }

    fun getBitmapFromHash(hash: String, fullWidth: Int? = null, fullHeight: Int? = null): Bitmap {
        val image = thumbHashToRGBA(Base64.decode(hash, Base64.DEFAULT))
        val bmp = Bitmap.createBitmap(image.width, image.height, Bitmap.Config.ARGB_8888)
        bmp.copyPixelsFromBuffer(ByteBuffer.wrap(image.rgba))
        fullWidth ?: return bmp
        fullHeight ?: return bmp
        return Bitmap.createScaledBitmap(bmp, fullWidth, fullHeight, true)
    }

    private fun bitmapToRgbaByteArray(bitmap: Bitmap): ByteArray {
        val width = bitmap.width
        val height = bitmap.height
        val pixels = IntArray(width * height)

        bitmap.getPixels(pixels, 0, width, 0, 0, width, height)

        val rgbaBytes = ByteArray(pixels.size * 4)
        for (i in pixels.indices) {
            val pixel = pixels[i]
            rgbaBytes[i * 4] = Color.red(pixel).toByte()
            rgbaBytes[i * 4 + 1] = Color.green(pixel).toByte()
            rgbaBytes[i * 4 + 2] = Color.blue(pixel).toByte()
            rgbaBytes[i * 4 + 3] = Color.alpha(pixel).toByte()
        }
        return rgbaBytes
    }

    private fun rgbaToThumbHash(w: Int, h: Int, rgba: ByteArray): ByteArray {
        require(!(w > MAX_WIDTH || h > MAX_HEIGHT)) { w.toString() + "x" + h + " doesn't fit in 100x100" }

        var avgR = 0f
        var avgG = 0f
        var avgB = 0f
        var avgA = 0f
        run {
            var i = 0
            var j = 0
            while (i < w * h) {
                val alpha = (rgba[j + 3].toInt() and 255) / 255.0f
                avgR += alpha / 255.0f * (rgba[j].toInt() and 255)
                avgG += alpha / 255.0f * (rgba[j + 1].toInt() and 255)
                avgB += alpha / 255.0f * (rgba[j + 2].toInt() and 255)
                avgA += alpha
                i++
                j += 4
            }
        }
        if (avgA > 0) {
            avgR /= avgA
            avgG /= avgA
            avgB /= avgA
        }
        val hasAlpha = avgA < w * h
        val lLimit = if (hasAlpha) 5 else 7
        val lx = max(1, ((lLimit * w).toFloat() / max(w, h).toFloat()).roundToInt())
        val ly = max(1, ((lLimit * h).toFloat() / max(w, h).toFloat()).roundToInt())
        val l = FloatArray(w * h)
        val p = FloatArray(w * h)
        val q = FloatArray(w * h)
        val a = FloatArray(w * h)

        var i = 0
        var j = 0
        while (i < w * h) {
            val alpha = (rgba[j + 3].toInt() and 255) / 255.0f
            val r = avgR * (1.0f - alpha) + alpha / 255.0f * (rgba[j].toInt() and 255)
            val g = avgG * (1.0f - alpha) + alpha / 255.0f * (rgba[j + 1].toInt() and 255)
            val b = avgB * (1.0f - alpha) + alpha / 255.0f * (rgba[j + 2].toInt() and 255)
            l[i] = (r + g + b) / 3.0f
            p[i] = (r + g) / 2.0f - b
            q[i] = r - g
            a[i] = alpha
            i++
            j += 4
        }

        val lChannel = ThumbHashChannel(max(3, lx), max(3, ly)).encode(w, h, l)
        val pChannel = ThumbHashChannel(3, 3).encode(w, h, p)
        val qChannel = ThumbHashChannel(3, 3).encode(w, h, q)
        val aChannel = if (hasAlpha) ThumbHashChannel(5, 5).encode(w, h, a) else null

        val isLandscape = w > h
        val header24 = ((63.0f * lChannel.dc).roundToInt()
                or ((31.5f + 31.5f * pChannel.dc).roundToInt() shl 6)
                or ((31.5f + 31.5f * qChannel.dc).roundToInt() shl 12)
                or ((31.0f * lChannel.scale).roundToInt() shl 18)
                or if (hasAlpha) 1 shl 23 else 0)
        val header16 = ((if (isLandscape) ly else lx)
                or ((63.0f * pChannel.scale).roundToInt() shl 3)
                or ((63.0f * qChannel.scale).roundToInt() shl 9)
                or if (isLandscape) 1 shl 15 else 0)
        val acStart = if (hasAlpha) 6 else 5
        val acCount = (lChannel.ac.size + pChannel.ac.size + qChannel.ac.size
                + if (hasAlpha) aChannel!!.ac.size else 0)
        val hash = ByteArray(acStart + (acCount + 1) / 2)
        hash[0] = header24.toByte()
        hash[1] = (header24 shr 8).toByte()
        hash[2] = (header24 shr 16).toByte()
        hash[3] = header16.toByte()
        hash[4] = (header16 shr 8).toByte()
        if (hasAlpha) hash[5] = ((15.0f * aChannel!!.dc).roundToInt()
                or ((15.0f * aChannel.scale).roundToInt() shl 4)).toByte()

        var acIndex = 0
        acIndex = lChannel.writeTo(hash, acStart, acIndex)
        acIndex = pChannel.writeTo(hash, acStart, acIndex)
        acIndex = qChannel.writeTo(hash, acStart, acIndex)
        if (hasAlpha) aChannel!!.writeTo(hash, acStart, acIndex)
        return hash
    }


    private fun thumbHashToRGBA(hash: ByteArray): ThumbHashImage {
        val header24 =
            hash[0].toInt() and 255 or (hash[1].toInt() and 255 shl 8) or (hash[2].toInt() and 255 shl 16)
        val header16 = hash[3].toInt() and 255 or (hash[4].toInt() and 255 shl 8)
        val lDc = (header24 and 63).toFloat() / 63.0f
        val pDc = (header24 shr 6 and 63).toFloat() / 31.5f - 1.0f
        val qDc = (header24 shr 12 and 63).toFloat() / 31.5f - 1.0f
        val lScale = (header24 shr 18 and 31).toFloat() / 31.0f
        val hasAlpha = header24 shr 23 != 0
        val pScale = (header16 shr 3 and 63).toFloat() / 63.0f
        val qScale = (header16 shr 9 and 63).toFloat() / 63.0f
        val isLandscape = header16 shr 15 != 0
        val lx = max(3, if (isLandscape) if (hasAlpha) 5 else 7 else header16 and 7)
        val ly = max(3, if (isLandscape) header16 and 7 else if (hasAlpha) 5 else 7)
        val aDc = if (hasAlpha) (hash[5].toInt() and 15).toFloat() / 15.0f else 1.0f
        val aScale = (hash[5].toInt() shr 4 and 15).toFloat() / 15.0f

        val acStart = if (hasAlpha) 6 else 5
        var acIndex = 0
        val lChannel = ThumbHashChannel(lx, ly)
        val pChannel = ThumbHashChannel(3, 3)
        val qChannel = ThumbHashChannel(3, 3)
        var aChannel: ThumbHashChannel? = null
        acIndex = lChannel.decode(hash, acStart, acIndex, lScale)
        acIndex = pChannel.decode(hash, acStart, acIndex, pScale * 1.25f)
        acIndex = qChannel.decode(hash, acStart, acIndex, qScale * 1.25f)
        if (hasAlpha) {
            aChannel = ThumbHashChannel(5, 5)
            aChannel.decode(hash, acStart, acIndex, aScale)
        }
        val lAc = lChannel.ac
        val pAc = pChannel.ac
        val qAc = qChannel.ac
        val aAc = if (hasAlpha) aChannel!!.ac else null

        val ratio = thumbHashToApproximateAspectRatio(hash)
        val w = (if (ratio > 1.0f) 32.0f else 32.0f * ratio).roundToInt()
        val h = (if (ratio > 1.0f) 32.0f / ratio else 32.0f).roundToInt()
        val rgba = ByteArray(w * h * 4)
        val cxStop = max(lx, if (hasAlpha) 5 else 3)
        val cyStop = max(ly, if (hasAlpha) 5 else 3)
        val fx = FloatArray(cxStop)
        val fy = FloatArray(cyStop)
        var y = 0
        var i = 0
        while (y < h) {
            var x = 0
            while (x < w) {
                var l = lDc
                var p = pDc
                var q = qDc
                var a = aDc

                for (cx in 0 until cxStop) fx[cx] =
                    cos(Math.PI / w * (x + 0.5f) * cx).toFloat()
                for (cy in 0 until cyStop) fy[cy] =
                    cos(Math.PI / h * (y + 0.5f) * cy).toFloat()

                run {
                    var cy = 0
                    var j = 0
                    while (cy < ly) {
                        val fy2 = fy[cy] * 2.0f
                        var cx = if (cy > 0) 0 else 1
                        while (cx * ly < lx * (ly - cy)) {
                            l += lAc[j] * fx[cx] * fy2
                            cx++
                            j++
                        }
                        cy++
                    }
                }

                var cy = 0
                var j = 0
                while (cy < 3) {
                    val fy2 = fy[cy] * 2.0f
                    var cx = if (cy > 0) 0 else 1
                    while (cx < 3 - cy) {
                        val f = fx[cx] * fy2
                        p += pAc[j] * f
                        q += qAc[j] * f
                        cx++
                        j++
                    }
                    cy++
                }

                if (hasAlpha) {
                    cy = 0
                    j = 0
                    while (cy < 5) {
                        val fy2 = fy[cy] * 2.0f
                        var cx = if (cy > 0) 0 else 1
                        while (cx < 5 - cy) {
                            a += aAc!![j] * fx[cx] * fy2
                            cx++
                            j++
                        }
                        cy++
                    }
                }

                val b = l - 2.0f / 3.0f * p
                val r = (3.0f * l - b + q) / 2.0f
                val g = r - q
                rgba[i] = max(0, (255.0f * min(1f, r)).roundToInt()).toByte()
                rgba[i + 1] = max(0, (255.0f * min(1f, g)).roundToInt()).toByte()
                rgba[i + 2] = max(0, (255.0f * min(1f, b)).roundToInt()).toByte()
                rgba[i + 3] = max(0, (255.0f * min(1f, a)).roundToInt()).toByte()
                x++
                i += 4
            }
            y++
        }
        return ThumbHashImage(w, h, rgba)
    }

    private fun thumbHashToAverageRGBA(hash: ByteArray): RGBA {
        val header =
            hash[0].toInt() and 255 or (hash[1].toInt() and 255 shl 8) or (hash[2].toInt() and 255 shl 16)
        val l = (header and 63).toFloat() / 63.0f
        val p = (header shr 6 and 63).toFloat() / 31.5f - 1.0f
        val q = (header shr 12 and 63).toFloat() / 31.5f - 1.0f
        val hasAlpha = header shr 23 != 0
        val a = if (hasAlpha) (hash[5].toInt() and 15).toFloat() / 15.0f else 1.0f
        val b = l - 2.0f / 3.0f * p
        val r = (3.0f * l - b + q) / 2.0f
        val g = r - q
        return RGBA(
            max(0f, min(1f, r)),
            max(0f, min(1f, g)),
            max(0f, min(1f, b)),
            a
        )
    }

    private fun thumbHashToApproximateAspectRatio(hash: ByteArray): Float {
        val header = hash[3]
        val hasAlpha = hash[2].toInt() and 0x80 != 0
        val isLandscape = hash[4].toInt() and 0x80 != 0
        val lx = if (isLandscape) if (hasAlpha) 5 else 7 else header.toInt() and 7
        val ly = if (isLandscape) header.toInt() and 7 else if (hasAlpha) 5 else 7
        return lx.toFloat() / ly.toFloat()
    }
}