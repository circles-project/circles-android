package com.futo.circles.ui.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import com.futo.circles.R
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class CirclesLogoView(
    context: Context,
    attrs: AttributeSet? = null,
) : View(context, attrs) {

    private val sideCirclesCount = 9
    private val defaultScale = 0.5f

    private val colors = listOf(
        R.color.green,
        R.color.purple,
        R.color.yellow,
        R.color.orange,
        R.color.red,
        R.color.pink,
        R.color.green,
        R.color.orange
    )

    private val circlesList = mutableListOf<CircleViewData>()

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if(circlesList.isEmpty()) circlesList.addAll(createCirclesList())
        circlesList.forEach { canvas?.drawCircle(it.x, it.y, it.radius, it.fillPaint) }
        circlesList.forEach{canvas?.drawCircle(it.x, it.y, it.radius, it.borderPaint)}
    }


    private fun createCirclesList(): List<CircleViewData> {
        val radius = min(measuredHeight, measuredWidth) * defaultScale * 0.5f
        val center = PointF(0.5f * measuredWidth, 0.5f * measuredHeight)
        val circlesList: MutableList<CircleViewData> = mutableListOf()

        circlesList.add(
            CircleViewData(
                center.x,
                center.y,
                radius,
                getFillPaint(R.color.blue, 0.95f),
                getCircleStrokePaint(8f)
            )
        )

        (1..sideCirclesCount).forEach { i ->
            circlesList.add(createCircle(center, radius, i))
        }
        return circlesList
    }


    private fun createCircle(center: PointF, radius: Float, number: Int): CircleViewData {
        val fraction = number.toDouble() / sideCirclesCount
        val angle = Math.toRadians(360 * fraction + 27)
        val rand = Random.nextDouble(0.5, 1.5).toFloat()
        val offset = (1.0f + 0.25f * rand) * radius
        val x = center.x + cos(angle).toFloat() * offset
        val y = center.y + sin(angle).toFloat() * offset
        val scale = 0.85f * rand * defaultScale
        val newRadius = scale * radius

        return CircleViewData(
            x,
            y,
            newRadius,
            getFillPaint(colors.random(), 0.55f),
            getCircleStrokePaint(4f)
        )
    }

    private fun getCircleStrokePaint(width: Float) = Paint().apply {
        isAntiAlias = true
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = width
    }

    private fun getFillPaint(@ColorRes fillColorId: Int, opacity: Float): Paint {
        val colorWithAlpha = ColorUtils.setAlphaComponent(
            ContextCompat.getColor(context, fillColorId),
            (255 * opacity).toInt()
        )

        return Paint().apply {
            isAntiAlias = true
            style = Paint.Style.FILL
            color = colorWithAlpha
        }
    }

}

data class CircleViewData(
    val x: Float,
    val y: Float,
    val radius: Float,
    val fillPaint: Paint,
    val borderPaint: Paint
)