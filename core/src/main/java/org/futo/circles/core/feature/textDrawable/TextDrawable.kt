package org.futo.circles.core.feature.textDrawable


import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Paint
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.graphics.drawable.shapes.RectShape
import android.graphics.drawable.shapes.RoundRectShape
import android.graphics.drawable.shapes.Shape
import androidx.annotation.ColorInt
import androidx.annotation.IntDef
import java.util.Locale
import kotlin.math.min


class TextDrawable private constructor(builder: Builder) : ShapeDrawable(builder.getShape()) {
    private var bitmap: Bitmap? = null
    private val borderColor: Int
    private val borderPaint: Paint
    private val borderThickness: Int
    private val fontSize: Int
    private val height: Int
    private val text: String
    private val textPaint: Paint
    private val radius: Float

    @TextDrawableShape
    private val shape: Int
    private val width: Int

    init {

        // shape properties
        shape = builder.shape
        height = builder.height
        width = builder.width
        radius = builder.radius

        // text and color
        text =
            if (builder.toUpperCase) builder.text.uppercase(Locale.getDefault()) else builder.text

        // text paint settings
        fontSize = builder.fontSize
        textPaint = Paint()
        textPaint.isAntiAlias = true
        textPaint.color = builder.textColor
        textPaint.isFakeBoldText = builder.isBold
        textPaint.strokeWidth = builder.borderThickness.toFloat()
        textPaint.style = Paint.Style.FILL
        textPaint.textAlign = Paint.Align.CENTER

        // border paint settings
        borderThickness = builder.borderThickness
        borderColor = builder.borderColor
        borderPaint = Paint()
        if (borderColor == -1) borderPaint.color =
            getDarkerShade(builder.color) else borderPaint.color =
            borderColor
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = borderThickness.toFloat()

        // drawable paint setColor
        val paint = paint
        paint.color = builder.color

        //custom centre drawable
        builder.drawable?.let {
            if (builder.drawable is BitmapDrawable) {
                bitmap = (builder.drawable as BitmapDrawable).bitmap
            } else {
                bitmap = Bitmap.createBitmap(
                    it.intrinsicWidth,
                    it.intrinsicHeight,
                    Bitmap.Config.ARGB_8888
                )
                val canvas = Canvas(bitmap!!)
                it.setBounds(0, 0, it.intrinsicWidth, it.intrinsicHeight)
                it.draw(canvas)
            }
        }
    }

    private fun getDarkerShade(@ColorInt color: Int): Int {
        return Color.rgb(
            (SHADE_FACTOR * Color.red(color)).toInt(),
            (SHADE_FACTOR * Color.green(color)).toInt(),
            (SHADE_FACTOR * Color.blue(color)).toInt()
        )
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)
        val r = bounds
        // draw border
        if (borderThickness > 0) {
            drawBorder(canvas)
        }
        val count = canvas.save()
        if (bitmap == null) {
            canvas.translate(r.left.toFloat(), r.top.toFloat())
        }
        // draw text
        val width = if (width < 0) r.width() else width
        val height = if (height < 0) r.height() else height
        val fontSize = if (fontSize < 0) min(width, height) / 2 else fontSize
        textPaint.textSize = fontSize.toFloat()
        val textBounds = Rect()
        textPaint.getTextBounds(text, 0, text.length, textBounds)
        canvas.drawText(
            text,
            (width / 2).toFloat(),
            height / 2 - textBounds.exactCenterY(),
            textPaint
        )
        if (bitmap == null) {
            textPaint.textSize = fontSize.toFloat()
            canvas.drawText(
                text, (width / 2).toFloat(),
                height / 2 - (textPaint.descent() + textPaint.ascent()) / 2, textPaint
            )
        } else {
            canvas.drawBitmap(
                bitmap!!,
                ((width - bitmap!!.width) / 2).toFloat(),
                ((height - bitmap!!.height) / 2).toFloat(),
                null
            )
        }
        canvas.restoreToCount(count)
    }

    private fun drawBorder(canvas: Canvas) {
        val rect = RectF(bounds)
        rect.inset((borderThickness / 2).toFloat(), (borderThickness / 2).toFloat())
        when (shape) {
            SHAPE_ROUND_RECT -> canvas.drawRoundRect(rect, radius, radius, borderPaint)
            SHAPE_ROUND -> canvas.drawOval(rect, borderPaint)
            SHAPE_RECT -> canvas.drawRect(rect, borderPaint)
            else -> canvas.drawRect(rect, borderPaint)
        }
    }

    override fun setAlpha(alpha: Int) {
        textPaint.alpha = alpha
    }

    override fun setColorFilter(cf: ColorFilter?) {
        textPaint.colorFilter = cf
    }

    override fun getIntrinsicWidth(): Int = width

    override fun getIntrinsicHeight(): Int = height

    fun getBitmap(): Bitmap {
        val bitmap: Bitmap = if (intrinsicWidth <= 0 || intrinsicHeight <= 0) {
            Bitmap.createBitmap(
                1,
                1,
                if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        } else {
            Bitmap.createBitmap(
                intrinsicWidth, intrinsicHeight,
                if (opacity != PixelFormat.OPAQUE) Bitmap.Config.ARGB_8888 else Bitmap.Config.RGB_565
            )
        }
        val canvas = Canvas(bitmap)
        setBounds(0, 0, canvas.width, canvas.height)
        draw(canvas)
        return bitmap
    }

    class Builder {
        internal var borderColor: Int
        internal var borderThickness = 0
        internal var color: Int
        var drawable: Drawable? = null
        internal var fontSize: Int
        internal var height: Int
        internal var isBold = false
        var radius = 0f
        internal var shape: Int
        internal var text = ""
        var textColor: Int
        internal val toUpperCase = false
        internal var width: Int

        init {
            borderColor = -1
            color = Color.GRAY
            fontSize = -1
            height = -1
            shape = SHAPE_RECT
            textColor = Color.WHITE
            width = -1
        }

        fun setBold(): Builder {
            isBold = true
            return this
        }

        fun setBorder(thickness: Int): Builder {
            borderThickness = thickness
            return this
        }

        fun setBorderColor(@ColorInt color: Int): Builder {
            borderColor = color
            return this
        }

        fun setColor(@ColorInt color: Int): Builder {
            this.color = color
            return this
        }

        fun setDrawable(drawable: Drawable): Builder {
            this.drawable = drawable
            return this
        }


        fun setFontSize(size: Int): Builder {
            fontSize = size
            return this
        }

        fun setHeight(height: Int): Builder {
            this.height = height
            return this
        }

        fun setRadius(radius: Int): Builder {
            this.radius = radius.toFloat()
            return this
        }

        fun setShape(@TextDrawableShape shape: Int): Builder {
            this.shape = shape
            return this
        }

        fun setText(text: String): Builder {
            this.text = text
            return this
        }

        fun setTextColor(@ColorInt color: Int): Builder {
            textColor = color
            return this
        }

        fun setWidth(width: Int): Builder {
            this.width = width
            return this
        }

        internal fun getShape(): Shape {
            return when (shape) {
                SHAPE_ROUND_RECT -> {
                    val radii =
                        floatArrayOf(radius, radius, radius, radius, radius, radius, radius, radius)
                    RoundRectShape(radii, null, null)
                }

                SHAPE_ROUND -> OvalShape()
                SHAPE_RECT -> RectShape()
                else -> RectShape()
            }
        }

        fun build(): TextDrawable {
            return TextDrawable(this)
        }
    }

    @IntDef(*[SHAPE_RECT, SHAPE_ROUND_RECT, SHAPE_ROUND])
    annotation class TextDrawableShape
    companion object {
        const val SHAPE_RECT = 0
        const val SHAPE_ROUND_RECT = 1
        const val SHAPE_ROUND = 2
        private const val SHADE_FACTOR = 0.9f
    }
}