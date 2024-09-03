package org.futo.circles.feature.timeline.list.holder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.model.PostListItem

abstract class TimelineListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    abstract fun bind(item: PostListItem)
}