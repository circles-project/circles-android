package org.futo.circles.core.base.list

import android.view.View
import androidx.recyclerview.widget.RecyclerView


interface StickyHeaderAdapter {

    fun getHeaderViewForItem(parent: RecyclerView, itemPosition: Int): View?

    fun isHeader(itemPosition: Int): Boolean
}