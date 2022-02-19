package com.futo.circles.ui.groups.list

import android.view.ViewGroup
import coil.load
import com.futo.circles.base.BaseRecyclerViewHolder
import com.futo.circles.databinding.GroupListItemBinding
import com.futo.circles.extensions.onClick
import org.matrix.android.sdk.api.session.group.model.GroupSummary
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class GroupViewHolder(
    parent: ViewGroup,
    onGroupClicked: (Int) -> Unit
) : BaseRecyclerViewHolder<RoomSummary, GroupListItemBinding>(
    parent,
    GroupListItemBinding::inflate
) {

    init {
        onClick(itemView) { position -> onGroupClicked(position) }
    }

    override fun bind(data: RoomSummary) {
        with(binding) {
            ivGroup.load(data.avatarUrl)
            tvGroupTitle.text = data.displayName
        }
    }
}