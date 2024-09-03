package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.databinding.ListItemTimelineLoadingBinding
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.model.TimelineLoadingItem

class TimelineLoadingViewHolder(
    parent: ViewGroup,
) : TimelineListItemViewHolder(inflate(parent, ListItemTimelineLoadingBinding::inflate)) {

    private companion object : ViewBindingHolder

    override fun bind(item: PostListItem) {
        if (item !is TimelineLoadingItem) return
    }
}