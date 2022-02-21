package com.futo.circles.ui.groups.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.ui.groups.timeline.list.GroupTimelineViewHolder
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class GroupTimelineAdapter(
) : BaseRvAdapter<TimelineEvent, GroupTimelineViewHolder>(DefaultDiffUtilCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GroupTimelineViewHolder = GroupTimelineViewHolder(parent)

    override fun onBindViewHolder(holder: GroupTimelineViewHolder, position: Int) {
        getItem(position)?.let { holder.bind(it) }
    }

}