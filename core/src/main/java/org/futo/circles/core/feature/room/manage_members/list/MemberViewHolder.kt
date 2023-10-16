package org.futo.circles.core.feature.room.manage_members.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemBannedMemberBinding
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.databinding.ListItemInvitedMemberBinding
import org.futo.circles.core.databinding.ListItemMemberBinding
import org.futo.circles.core.extensions.getRoleNameResId
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.isCurrentUserAbleToBan
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.feature.room.manage_members.ManageMembersOptionsListener
import org.futo.circles.core.model.BannedMemberListItem
import org.futo.circles.core.model.GroupMemberListItem
import org.futo.circles.core.model.InvitedMemberListItem
import org.futo.circles.core.model.ManageMembersHeaderListItem
import org.futo.circles.core.model.ManageMembersListItem

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
            vUser.bind(data.user)
            if (data.isOptionsAvailable) {
                ivOptionsArrow.visible()
                ivOptionsArrow.setImageResource(
                    if (data.isOptionsOpened) org.futo.circles.core.R.drawable.ic_keyboard_arrow_up
                    else org.futo.circles.core.R.drawable.ic_keyboard_arrow_down
                )
                onClick(binding.contentLayout) { position -> onUserClicked(position) }
                with(optionsView) {
                    setListener(manageMembersListener)
                    setIsVisible(data.isOptionsOpened)
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

class InvitedMemberViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit,
    private val manageMembersListener: ManageMembersOptionsListener
) : ManageMembersViewHolder(inflate(parent, ListItemInvitedMemberBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedMemberBinding

    override fun bind(data: ManageMembersListItem) {
        if (data !is InvitedMemberListItem) return

        with(binding) {
            vUser.bind(data.user)
            if (data.isOptionsAvailable) {
                ivOptionsArrow.visible()
                ivOptionsArrow.setImageResource(
                    if (data.isOptionsOpened) org.futo.circles.core.R.drawable.ic_keyboard_arrow_up
                    else org.futo.circles.core.R.drawable.ic_keyboard_arrow_down
                )
                onClick(binding.contentLayout) { position -> onUserClicked(position) }
                binding.optionsLayout.setIsVisible(data.isOptionsOpened)
                binding.btnRemove.setOnClickListener {
                    manageMembersListener.cancelPendingInvitation(data.user.id)
                }
                binding.btnResend.setOnClickListener {
                    manageMembersListener.resendInvitation(data.user.id)
                }
            } else {
                ivOptionsArrow.gone()
                binding.contentLayout.setOnClickListener(null)
            }
        }
    }
}


class BannedMemberViewHolder(
    parent: ViewGroup,
    private val manageMembersListener: ManageMembersOptionsListener,
) : ManageMembersViewHolder(inflate(parent, ListItemBannedMemberBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemBannedMemberBinding

    override fun bind(data: ManageMembersListItem) {
        if (data !is BannedMemberListItem) return

        binding.lUser.bind(data.user)

        val isAbleToBan = data.powerLevelsContent.isCurrentUserAbleToBan()
        binding.ivRemove.setIsVisible(isAbleToBan)
        if (isAbleToBan) binding.ivRemove.setOnClickListener {
            manageMembersListener.unBanUser(data.user.id)
        }
    }
}