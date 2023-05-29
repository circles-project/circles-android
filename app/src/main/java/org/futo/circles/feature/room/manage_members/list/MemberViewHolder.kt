package org.futo.circles.feature.room.manage_members.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.extensions.getRoleNameResId
import org.futo.circles.core.extensions.isCurrentUserAbleToBan
import org.futo.circles.core.extensions.isCurrentUserAbleToKick
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.databinding.ListItemMemberBinding
import org.futo.circles.databinding.ListItemNotJoinedUserBinding
import org.futo.circles.extensions.gone
import org.futo.circles.extensions.onClick
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.extensions.visible
import org.futo.circles.feature.room.ManageMembersOptionsListener
import org.futo.circles.model.GroupMemberListItem
import org.futo.circles.model.ManageMembersHeaderListItem
import org.futo.circles.model.ManageMembersListItem
import org.futo.circles.model.NotJoinedUserListItem
import org.matrix.android.sdk.api.session.room.model.Membership

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
                    if (data.isOptionsOpened) R.drawable.ic_keyboard_arrow_up
                    else R.drawable.ic_keyboard_arrow_down
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

class NotJoinedUserViewHolder(
    parent: ViewGroup,
    private val manageMembersListener: ManageMembersOptionsListener,
) : ManageMembersViewHolder(inflate(parent, ListItemNotJoinedUserBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemNotJoinedUserBinding

    override fun bind(data: ManageMembersListItem) {
        if (data !is NotJoinedUserListItem) return

        binding.lUser.bind(data.user)

        when (data.membership) {
            Membership.INVITE -> {
                binding.tvStatus.text = context.getString(R.string.invited)
                val isAbleToKick = data.powerLevelsContent.isCurrentUserAbleToKick()
                binding.ivRemove.setIsVisible(isAbleToKick)
                if (isAbleToKick) binding.ivRemove.setOnClickListener {
                    manageMembersListener.cancelPendingInvitation(data.user.id)
                }
            }

            Membership.BAN -> {
                binding.tvStatus.text = context.getString(R.string.banned)
                val isAbleToBan = data.powerLevelsContent.isCurrentUserAbleToBan()
                binding.ivRemove.setIsVisible(isAbleToBan)
                if (isAbleToBan) binding.ivRemove.setOnClickListener {
                    manageMembersListener.unBanUser(data.user.id)
                }
            }

            else -> return
        }
    }
}