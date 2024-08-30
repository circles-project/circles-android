package org.futo.circles.view.members_list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.CirclesUserSummary

class MembersAvatarsListAdapter :
    BaseRvAdapter<CirclesUserSummary, MembersAvatarsViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup, viewType: Int
    ) = MembersAvatarsViewHolder(parent = parent)

    override fun onBindViewHolder(holder: MembersAvatarsViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}