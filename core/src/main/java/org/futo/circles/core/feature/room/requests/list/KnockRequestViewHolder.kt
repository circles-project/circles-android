package org.futo.circles.core.feature.room.requests.list

import android.view.ViewGroup
import org.futo.circles.core.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemKnockRequestBinding
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.KnockRequestListItem
import org.futo.circles.core.model.RoomRequestListItem
import org.futo.circles.core.model.toCircleUser


class KnockRequestViewHolder(
    parent: ViewGroup,
    onRequestClicked: (Int, Boolean) -> Unit
) : RoomRequestViewHolder(inflate(parent, ListItemKnockRequestBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemKnockRequestBinding

    init {
        onClick(binding.btnInvite) { position -> onRequestClicked(position, true) }
        onClick(binding.btnDecline) { position -> onRequestClicked(position, false) }
    }

    override fun bind(data: RoomRequestListItem) {
        if (data !is KnockRequestListItem) return
        with(binding) {
            setLoading(data.isLoading)
            tvRoomName.text = getRoomNameMessage(data.roomType, data.roomName)
            tvReason.apply {
                setIsVisible(!data.message.isNullOrBlank())
                text = data.message
            }
            vUserLayout.bind(data.toCircleUser())
        }
    }

    private fun getRoomNameMessage(roomType: CircleRoomTypeArg, roomName: String): String {
        val roomTypeName = context.getString(
            when (roomType) {
                CircleRoomTypeArg.Circle -> R.string.circle
                CircleRoomTypeArg.Group -> R.string.group
                CircleRoomTypeArg.Photo -> R.string.gallery
            }
        )
        return context.getString(R.string.requesting_invitation_to_format, roomTypeName, roomName)
    }

    private fun setLoading(isLoading: Boolean) {
        with(binding) {
            vLoading.setIsVisible(isLoading)
            btnInvite.setIsVisible(!isLoading)
            btnDecline.setIsVisible(!isLoading)
        }
    }
}