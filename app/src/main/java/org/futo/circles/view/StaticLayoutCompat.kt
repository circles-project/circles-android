package org.futo.circles.view

import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.text.TextUtils
import androidx.annotation.FloatRange

internal class StaticLayoutCompat {

    class Builder(
        private val text: CharSequence,
        private val start: Int,
        private val end: Int,
        private val paint: TextPaint,
        private val width: Int
    ) {
        constructor(text: CharSequence, paint: TextPaint, width: Int) :
                this(text, 0, text.length, paint, width)

        private var alignment: Layout.Alignment = Layout.Alignment.ALIGN_NORMAL
        private var spacingMult = 1f
        private var spacingAdd = 0f
        private var includePad = true
        private var ellipsizedWidth = width
        private var ellipsize: TextUtils.TruncateAt? = null
        private var maxLines = Integer.MAX_VALUE


        fun setLineSpacing(
            spacingAdd: Float,
            @FloatRange(from = 0.0) spacingMult: Float
        ): Builder {
            this.spacingAdd = spacingAdd
            this.spacingMult = spacingMult
            return this
        }

        fun setIncludePad(includePad: Boolean): Builder {
            this.includePad = includePad
            return this
        }

        fun build(): StaticLayout {
            return StaticLayout.Builder
                .obtain(text, start, end, paint, width)
                .setAlignment(alignment)
                .setLineSpacing(spacingAdd, spacingMult)
                .setIncludePad(includePad)
                .setEllipsize(ellipsize)
                .setEllipsizedWidth(ellipsizedWidth)
                .setMaxLines(maxLines)
                .build()
        }
    }
}