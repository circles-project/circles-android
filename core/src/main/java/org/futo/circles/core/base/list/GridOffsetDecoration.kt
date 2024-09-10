package org.futo.circles.core.base.list

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

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
        val column = (view.layoutParams as? GridLayoutManager.LayoutParams)?.spanIndex ?: 0
        val rect = offsetFunction.invoke(holder, column)
        outRect.left = rect.left
        outRect.right = rect.right
        outRect.top = rect.top
        outRect.bottom = rect.bottom
    }
}