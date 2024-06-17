package org.futo.circles.core.feature.room.requests.list

import android.view.ViewGroup
import org.futo.circles.core.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInvitedCircleBinding
import org.futo.circles.core.databinding.ListItemInvitedGalleryBinding
import org.futo.circles.core.databinding.ListItemInvitedGroupBinding
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsEncryptedIcon
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.RoomInviteListItem
import org.futo.circles.core.model.RoomRequestListItem

class InvitedGroupViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : RoomRequestViewHolder(inflate(parent, ListItemInvitedGroupBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedGroupBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivGroup) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: RoomRequestListItem) {
        if (data !is RoomInviteListItem) return
        with(binding) {
            setLoading(data.isLoading)
            ivGroup.loadRoomProfileIcon(
                data.info.avatarUrl,
                data.info.title,
                applyBlur = data.shouldBlurIcon
            )
            tvShowProfileImage.setIsVisible(data.shouldBlurIcon)
            ivLock.setIsEncryptedIcon(data.isEncrypted)
            tvGroupTitle.text = data.info.title
            tvInviterName.text = context.getString(
                R.string.invited_by_format,
                data.inviterName
            )
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

class InvitedCircleViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : RoomRequestViewHolder(inflate(parent, ListItemInvitedCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedCircleBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivCircle) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: RoomRequestListItem) {
        if (data !is RoomInviteListItem) return
        with(binding) {
            setLoading(data.isLoading)
            tvShowProfileImage.setIsVisible(data.shouldBlurIcon)
            ivCircle.loadRoomProfileIcon(
                data.info.avatarUrl,
                data.info.title,
                applyBlur = data.shouldBlurIcon
            )
            tvCircleTitle.text = data.info.title
            binding.tvInvitedBy.text =
                context.getString(
                    R.string.invited_by_format,
                    data.inviterName
                )
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

class InvitedGalleryViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : RoomRequestViewHolder(inflate(parent, ListItemInvitedGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedGalleryBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivGallery) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: RoomRequestListItem) {
        if (data !is RoomInviteListItem) return
        with(binding) {
            setLoading(data.isLoading)
            tvGalleryTitle.text = data.info.title
            ivGallery.loadRoomProfileIcon(
                data.info.avatarUrl,
                data.info.title,
                applyBlur = data.shouldBlurIcon
            )
            tvShowProfileImage.setIsVisible(data.shouldBlurIcon)
            tvInviterName.text = context.getString(R.string.invited_by_format, data.inviterName)
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