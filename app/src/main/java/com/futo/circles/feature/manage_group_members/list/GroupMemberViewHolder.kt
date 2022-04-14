package com.futo.circles.feature.manage_group_members.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.GroupMemberListItemBinding
import com.futo.circles.databinding.InviteHeaderListItemBinding
import com.futo.circles.databinding.InvitedUserListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setVisibility
import com.futo.circles.model.GroupMemberListItem
import com.futo.circles.model.InvitedUserListItem
import com.futo.circles.model.ManageMembersHeaderListItem
import com.futo.circles.model.ManageMembersListItem
import com.futo.circles.view.ManageMembersOptionsListener

abstract class ManageMembersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: ManageMembersListItem)
}


class GroupMemberViewHolder(
    parent: ViewGroup,
    onUserClicked: (Int) -> Unit,
    manageMembersListener: ManageMembersOptionsListener
) : ManageMembersViewHolder(inflate(parent, GroupMemberListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GroupMemberListItemBinding

    init {
        onClick(binding.contentLayout) { position -> onUserClicked(position) }
        binding.optionsView.setListener(manageMembersListener)
    }

    override fun bind(data: ManageMembersListItem) {
        if (data !is GroupMemberListItem) return

        with(binding) {
            tvRole.text = context.getString(data.getRoleNameResId())
            with(lUser) {
                ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
                tvUserName.text = data.user.name
                tvUserId.text = data.id
            }
            ivOptionsArrow.setImageResource(
                if (data.isOptionsOpened) R.drawable.ic_keyboard_arrow_up
                else R.drawable.ic_keyboard_arrow_down
            )
            with(optionsView) {
                setVisibility(data.isOptionsOpened)
                setData(data.id)
            }
        }
    }
}

class ManageMembersHeaderViewHolder(
    parent: ViewGroup,
) : ManageMembersViewHolder(inflate(parent, InviteHeaderListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InviteHeaderListItemBinding

    override fun bind(data: ManageMembersListItem) {
        if (data !is ManageMembersHeaderListItem) return
        binding.tvHeader.text = data.name
    }
}

class InvitedUserViewHolder(
    parent: ViewGroup,
    onCancelInvitation: (Int) -> Unit,
) : ManageMembersViewHolder(inflate(parent, InvitedUserListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InvitedUserListItemBinding

    init {
        onClick(binding.ivCancelInvite) { position -> onCancelInvitation(position) }
    }

    override fun bind(data: ManageMembersListItem) {
        if (data !is InvitedUserListItem) return

        with(binding.lUser) {
            ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
            tvUserName.text = data.user.name
            tvUserId.text = data.id
        }
    }
}