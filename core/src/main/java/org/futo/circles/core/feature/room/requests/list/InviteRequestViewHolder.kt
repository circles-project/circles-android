package org.futo.circles.core.feature.room.requests.list

import android.view.ViewGroup
import org.futo.circles.core.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteRequestBinding
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.RoomRequestListItem
import org.futo.circles.core.model.RoomRequestTypeArg

class InviteRequestViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : RoomRequestViewHolder(inflate(parent, ListItemInviteRequestBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteRequestBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
    }

    override fun bind(data: RoomRequestListItem) {
        if (data !is RoomInviteListItem) return
        with(binding) {
            setLoading(data.isLoading)
            ivImage.loadRoomProfileIcon(data.info.avatarUrl, data.info.title)
            tvTitle.text = data.info.title
            tvInvitedBy.text = if (data.requestType == RoomRequestTypeArg.DM) {
                context.getString(R.string.invited_you_for_direct_messages)
            } else {
                context.getString(R.string.invited_by_format, data.inviterName)
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        with(binding) {
            vLoading.setIsVisible(isLoading)
            btnAccept.setIsVisible(!isLoading)
            btnDecline.setIsVisible(!isLoading)
        }
    }
}


