package com.futo.circles.core.list

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView


class RoomsDividerDecoration(context: Context, color: Int, heightDp: Float) :
    RecyclerView.ItemDecoration() {
    private val paint: Paint = Paint()
    private val dividerHeightDp: Int
    private var escapeViewType: Int = -1

    constructor(context: Context, viewTypeToEscape: Int) : this(
        context,
        Color.argb((255 * 0.2).toInt(), 0, 0, 0),
        1f
    ) {
        escapeViewType = viewTypeToEscape
    }

    init {
        paint.style = Paint.Style.FILL
        paint.color = color
        dividerHeightDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            heightDp,
            context.resources.displayMetrics
        ).toInt()

    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.set(0, 0, 0, dividerHeightDp)
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (i in 0 until parent.childCount) {
            val view: View = parent.getChildAt(i)
            val position: Int = parent.getChildAdapterPosition(view)
            val viewType =
                position.takeIf { it != -1 }?.let { parent.adapter?.getItemViewType(position) }
            if (viewType != escapeViewType) {
                c.drawRect(
                    view.left.toFloat(),
                    view.bottom.toFloat(),
                    view.right.toFloat(),
                    (view.bottom + dividerHeightDp).toFloat(),
                    paint
                )
            }
        }
    }
}