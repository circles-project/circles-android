package com.futo.circles.feature.manage_group_members.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.base.context
import com.futo.circles.databinding.GroupMemberListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.model.GroupMemberListItem

class GroupMemberViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(inflate(parent, GroupMemberListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GroupMemberListItemBinding


    fun bind(data: GroupMemberListItem) {
        binding.tvRole.text = context.getString(data.getRoleNameResId())

        with(binding.lUser) {
            ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
            tvUserName.text = data.user.name
            tvUserId.text = data.id
        }
    }
}