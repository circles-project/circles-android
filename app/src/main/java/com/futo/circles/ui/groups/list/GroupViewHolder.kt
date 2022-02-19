package com.futo.circles.ui.groups.list

import android.view.ViewGroup
import coil.load
import com.futo.circles.R
import com.futo.circles.base.BaseRecyclerViewHolder
import com.futo.circles.databinding.GroupListItemBinding
import com.futo.circles.extensions.onClick
import org.matrix.android.sdk.api.session.group.model.GroupSummary

class GroupViewHolder(
    parent: ViewGroup,
    onGroupClicked: (Int) -> Unit
) : BaseRecyclerViewHolder<GroupSummary, GroupListItemBinding>(parent, R.layout.group_list_item) {

    init {
        onClick(itemView) { position -> onGroupClicked(position) }
    }

    override fun bind(data: GroupSummary) {
        with(binding) {
            ivGroup.load(data.avatarUrl)
            tvGroupTitle.text = data.displayName
        }
    }
}