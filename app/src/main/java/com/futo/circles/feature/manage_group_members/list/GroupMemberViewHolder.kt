package com.futo.circles.feature.manage_group_members.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.GroupMemberListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setVisibility
import com.futo.circles.model.GroupMemberListItem
import com.futo.circles.view.ManageMembersOptionsListener

class GroupMemberViewHolder(
    parent: ViewGroup,
    onUserClicked: (Int) -> Unit,
    manageMembersListener: ManageMembersOptionsListener
) : RecyclerView.ViewHolder(inflate(parent, GroupMemberListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GroupMemberListItemBinding

    init {
        onClick(binding.contentLayout) { position -> onUserClicked(position) }
        binding.optionsView.setListener(manageMembersListener)
    }

    fun bind(data: GroupMemberListItem) {
        binding.tvRole.text = context.getString(data.getRoleNameResId())

        with(binding.lUser) {
            ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
            tvUserName.text = data.user.name
            tvUserId.text = data.id
        }
        binding.ivOptionsArrow.setImageResource(
            if (data.isOptionsOpened) R.drawable.ic_keyboard_arrow_up
            else R.drawable.ic_keyboard_arrow_down
        )
        binding.optionsView.setVisibility(data.isOptionsOpened)
        binding.optionsView.setData(data.id)
    }
}