package org.futo.circles.core.base.list

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView


abstract class BaseRvDecoration<in T : RecyclerView.ViewHolder> : RecyclerView.ItemDecoration() {

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) =
        if (shouldDraw()) draw(c, parent, state) else super.onDraw(c, parent, state)

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) =
        if (shouldDrawOver()) drawOver(c, parent, state) else super.onDrawOver(c, parent, state)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        @Suppress("UNCHECKED_CAST")
        val holder = parent.getChildViewHolder(view) as? T

        holder?.takeIf(::shouldOffsetHolder)?.let { offsetHolder(outRect, holder, parent, state) }
            ?: super.getItemOffsets(outRect, view, parent, state)
    }

    open fun shouldDraw(): Boolean = false
    open fun shouldDrawOver(): Boolean = false
    open fun shouldOffsetHolder(holder: T): Boolean = false

    open fun draw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

    }

    open fun drawOver(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {

    }

    open fun offsetHolder(
        outRect: Rect,
        holder: T,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {

    }

    class OffsetDecoration<in T : RecyclerView.ViewHolder>(
        private val topOffset: Int = 0,
        private val bottomOffset: Int = 0,
        private val leftOffset: Int = 0,
        private val rightOffset: Int = 0
    ) : BaseRvDecoration<T>() {
        override fun shouldOffsetHolder(holder: T): Boolean = true

        constructor(verticalOffset: Int, horizontalOffset: Int) : this(
            topOffset = verticalOffset,
            bottomOffset = verticalOffset,
            leftOffset = horizontalOffset,
            rightOffset = horizontalOffset
        )

        constructor(offset: Int) : this(
            topOffset = offset,
            bottomOffset = offset,
            leftOffset = offset,
            rightOffset = offset
        )

        override fun offsetHolder(
            outRect: Rect,
            holder: T,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = leftOffset
            outRect.right = rightOffset
            outRect.top = topOffset
            outRect.bottom = bottomOffset
        }
    }

}



