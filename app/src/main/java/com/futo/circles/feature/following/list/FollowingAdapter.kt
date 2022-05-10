package com.futo.circles.feature.following.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.feature.groups.list.GroupViewHolder
import com.futo.circles.model.FollowingListItem
import com.futo.circles.model.GroupListItem

class FollowingAdapter(
) : BaseRvAdapter<FollowingListItem, FollowingViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FollowingViewHolder = FollowingViewHolder(parent = parent)

    override fun onBindViewHolder(holder: FollowingViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}