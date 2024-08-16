package org.futo.circles.core.feature.user.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.feature.user.list.TimelineViewType.Header
import org.futo.circles.core.feature.user.list.TimelineViewType.MutualFriend
import org.futo.circles.core.feature.user.list.TimelineViewType.Room
import org.futo.circles.core.model.MutualFriendListItem
import org.futo.circles.core.model.TimelineHeaderItem
import org.futo.circles.core.model.TimelineListItem
import org.futo.circles.core.model.TimelineRoomListItem

private enum class TimelineViewType { Header, Room, MutualFriend }

class UsersCirclesAdapter(
    private val onUnFollow: (String) -> Unit,
    private val onUserClicked: (String) -> Unit
) : BaseRvAdapter<TimelineListItem, UserTimelineViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is TimelineHeaderItem -> Header
        is TimelineRoomListItem -> Room
        is MutualFriendListItem -> MutualFriend
    }.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UserTimelineViewHolder = when (TimelineViewType.entries[viewType]) {
        Header -> UserTimelineHeaderViewHolder(parent)
        Room -> UsersTimelineRoomViewHolder(
            parent,
            onUnFollow = { position -> onUnFollow(getItem(position).id) },
        )

        MutualFriend -> MutualFriendsViewHolder(
            parent,
            onUserClicked = { position -> onUserClicked(getItem(position).id) }
        )
    }


    override fun onBindViewHolder(holder: UserTimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}