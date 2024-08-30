package org.futo.circles.core.base.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder


open class OffsetDecoration(
    private val topOffset: Int = 0,
    private val bottomOffset: Int = 0,
    private val leftOffset: Int = 0,
    private val rightOffset: Int = 0
) : RecyclerView.ItemDecoration() {


    constructor(
        verticalOffset: Int,
        horizontalOffset: Int
    ) : this(
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

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val holder = parent.getChildViewHolder(view)
        holder?.let { offsetHolder(outRect) }
            ?: super.getItemOffsets(outRect, view, parent, state)
    }

    private fun offsetHolder(outRect: Rect) {
        outRect.left = leftOffset
        outRect.right = rightOffset
        outRect.top = topOffset
        outRect.bottom = bottomOffset
    }
}

class GridOffsetDecoration(
    private val offsetFunction: (ViewHolder, Int) -> Rect
) : OffsetDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val holder = parent.getChildViewHolder(view)
        val position = parent.getChildAdapterPosition(view)
        val rect = offsetFunction.invoke(holder, position)
        outRect.left = rect.left
        outRect.right = rect.right
        outRect.top = rect.top
        outRect.bottom = rect.bottom
    }
}



