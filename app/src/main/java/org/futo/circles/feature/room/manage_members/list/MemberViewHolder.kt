package org.futo.circles.feature.room.manage_members.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.databinding.ListItemInvitedUserBinding
import org.futo.circles.databinding.ListItemMemberBinding
import org.futo.circles.extensions.*
import org.futo.circles.model.GroupMemberListItem
import org.futo.circles.model.InvitedUserListItem
import org.futo.circles.model.ManageMembersHeaderListItem
import org.futo.circles.model.ManageMembersListItem
import org.futo.circles.view.ManageMembersOptionsListener

abstract class ManageMembersViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: ManageMembersListItem)
}


class MemberViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit,
    private val manageMembersListener: ManageMembersOptionsListener
) : ManageMembersViewHolder(inflate(parent, ListItemMemberBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMemberBinding

    override fun bind(data: ManageMembersListItem) {
        if (data !is GroupMemberListItem) return

        with(binding) {
            tvRole.text = context.getString(data.role.getRoleNameResId())
            with(lUser) {
                ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
                tvUserName.text = data.user.name
                tvUserId.text = data.id
            }
            if (data.isOptionsAvailable) {
                ivOptionsArrow.visible()
                ivOptionsArrow.setImageResource(
                    if (data.isOptionsOpened) R.drawable.ic_keyboard_arrow_up
                    else R.drawable.ic_keyboard_arrow_down
                )
                onClick(binding.contentLayout) { position -> onUserClicked(position) }
                with(optionsView) {
                    setListener(manageMembersListener)
                    setIsVisible(data.isOptionsOpened && data.isOptionsAvailable)
                    setData(data.id, data.powerLevelsContent)
                }
            } else {
                ivOptionsArrow.gone()
                binding.contentLayout.setOnClickListener(null)
            }
        }
    }
}

class ManageMembersHeaderViewHolder(
    parent: ViewGroup,
) : ManageMembersViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: ManageMembersListItem) {
        if (data !is ManageMembersHeaderListItem) return
        binding.tvHeader.text = data.name
    }
}

class InvitedUserViewHolder(
    parent: ViewGroup,
    private val onCancelInvitation: (Int) -> Unit,
) : ManageMembersViewHolder(inflate(parent, ListItemInvitedUserBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedUserBinding

    override fun bind(data: ManageMembersListItem) {
        if (data !is InvitedUserListItem) return

        with(binding.ivCancelInvite) {
            if (data.powerLevelsContent.isCurrentUserAbleToKick()) {
                onClick(this) { position -> onCancelInvitation(position) }
                visible()
            } else {
                setOnClickListener(null)
                gone()
            }
        }

        with(binding.lUser) {
            ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
            tvUserName.text = data.user.name
            tvUserId.text = data.id
        }
    }
}