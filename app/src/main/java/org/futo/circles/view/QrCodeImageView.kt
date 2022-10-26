package org.futo.circles.view

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.AttributeSet
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatImageView
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter

class QrCodeImageView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private var data: String? = null

    fun setData(data: String) {
        this.data = data

        render()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        render()
    }

    private fun render() {
        data
            ?.takeIf { height > 0 }
            ?.let {
                val bitmap = it.toBitMatrix(height).toBitmap()
                post { setImageBitmap(bitmap) }
            }
    }
}

private fun String.toBitMatrix(size: Int): BitMatrix {
    return QRCodeWriter().encode(
        this,
        BarcodeFormat.QR_CODE,
        size,
        size
    )
}

private fun BitMatrix.toBitmap(
    @ColorInt backgroundColor: Int = Color.WHITE,
    @ColorInt foregroundColor: Int = Color.BLACK
): Bitmap {
    val colorBuffer = IntArray(width * height)
    var rowOffset = 0
    for (y in 0 until height) {
        for (x in 0 until width) {
            val arrayIndex = x + rowOffset
            colorBuffer[arrayIndex] = if (get(x, y)) foregroundColor else backgroundColor
        }
        rowOffset += width
    }

    return Bitmap.createBitmap(colorBuffer, width, height, Bitmap.Config.ARGB_8888)
}