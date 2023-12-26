package org.futo.circles.core.feature.room.invites.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInvitedCircleBinding
import org.futo.circles.core.databinding.ListItemInvitedGalleryBinding
import org.futo.circles.core.databinding.ListItemInvitedGroupBinding
import org.futo.circles.core.databinding.ListItemPeopleRequestBinding
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsEncryptedIcon
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.model.FollowRequestListItem
import org.futo.circles.core.model.InviteListItem
import org.futo.circles.core.model.RoomInviteListItem

abstract class InviteViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: InviteListItem)
}


class InvitedGroupViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : InviteViewHolder(inflate(parent, ListItemInvitedGroupBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedGroupBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivGroup) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: InviteListItem) {
        if (data !is RoomInviteListItem) return

        with(binding) {
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
}

class InvitedCircleViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : InviteViewHolder(inflate(parent, ListItemInvitedCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedCircleBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivCircle) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: InviteListItem) {
        if (data !is RoomInviteListItem) return

        with(binding) {
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
}

class InvitedGalleryViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : InviteViewHolder(inflate(parent, ListItemInvitedGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedGalleryBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivGallery) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: InviteListItem) {
        if (data !is RoomInviteListItem) return

        with(binding) {
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
}

class FollowRequestViewHolder(
    parent: ViewGroup,
    private val onRequestClicked: (Int, Boolean) -> Unit
) : InviteViewHolder(inflate(parent, ListItemPeopleRequestBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleRequestBinding

    init {
        onClick(binding.btnAccept) { position -> onRequestClicked(position, true) }
        onClick(binding.btnDecline) { position -> onRequestClicked(position, false) }
    }

    override fun bind(data: InviteListItem) {
        val user = (data as? FollowRequestListItem)?.user ?: return
        bindUser(user)
        binding.tvReasonMessage.apply {
            setIsVisible(data.reasonMessage != null)
            text = data.reasonMessage
        }
    }

    private fun bindUser(user: CirclesUserSummary) {
        with(binding) {
            tvUserName.text = user.name
            ivUserImage.loadUserProfileIcon(user.avatarUrl, user.id)
        }
    }
}
