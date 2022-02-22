package com.futo.circles.ui.groups.timeline.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.ui.groups.timeline.model.GroupMessage
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class GroupTimelineAdapter(
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GroupMessage, GroupTimelineViewHolder>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupTimelineViewHolder = GroupTimelineViewHolder(parent)

    override fun onBindViewHolder(holder: GroupTimelineViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

}