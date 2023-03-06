package org.futo.circles.feature.people.user.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.feature.people.user.list.TimelineViewType.*
import org.futo.circles.model.TimelineHeaderItem
import org.futo.circles.model.TimelineListItem
import org.futo.circles.model.TimelineRoomListItem

private enum class TimelineViewType { Header, Room }
class UsersCirclesAdapter :
    BaseRvAdapter<TimelineListItem, UserTimelineViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is TimelineHeaderItem -> Header
        is TimelineRoomListItem -> Room
    }.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserTimelineViewHolder = when (values()[viewType]) {
        Header -> UserTimelineHeaderViewHolder(parent)
        Room -> UsersTimelineRoomViewHolder(parent)
    }


    override fun onBindViewHolder(holder: UserTimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}