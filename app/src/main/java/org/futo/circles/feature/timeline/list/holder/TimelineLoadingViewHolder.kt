package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.model.TimelineLoadingItem
import org.futo.circles.databinding.ListItemTimelineLoadingBinding

class TimelineLoadingViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(inflate(parent, ListItemTimelineLoadingBinding::inflate)),
    PostListItemViewHolder {

    private companion object : ViewBindingHolder

    override fun bind(item: PostListItem) {
        if (item !is TimelineLoadingItem) return
    }
}