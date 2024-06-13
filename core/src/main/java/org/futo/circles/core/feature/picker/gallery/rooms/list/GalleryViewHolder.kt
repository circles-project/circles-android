package org.futo.circles.core.feature.picker.gallery.rooms.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.databinding.ListItemInviteNotificationBinding
import org.futo.circles.core.databinding.ListItemJoinedGalleryBinding
import org.futo.circles.core.extensions.loadMatrixImage
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.feature.textDrawable.TextDrawable
import org.futo.circles.core.model.GalleryHeaderItem
import org.futo.circles.core.model.GalleryInvitesNotificationListItem
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem
import org.futo.circles.core.utils.TextFormatUtils


abstract class GalleryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: GalleryListItem)
}

class JoinedGalleryViewHolder(
    parent: ViewGroup,
    onGalleryClicked: (Int) -> Unit
) : GalleryViewHolder(inflate(parent, ListItemJoinedGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemJoinedGalleryBinding

    init {
        onClick(itemView) { position -> onGalleryClicked(position) }
    }

    override fun bind(data: GalleryListItem) {
        if (data !is JoinedGalleryListItem) return

        with(binding) {
            val placeholder = TextDrawable.Builder()
                .setShape(TextDrawable.SHAPE_ROUND_RECT)
                .setColor(ColorGenerator().getColor(data.id))
                .build()
            ivGalleryImage.loadMatrixImage(url = data.info.avatarUrl, placeholder = placeholder)
            tvGalleryName.text = data.info.title
            lUser.setIsVisible(!data.isMyGallery())
            data.roomOwner?.let {
                tvUserName.text = it.displayName
                ivUserImage.loadUserProfileIcon(it.avatarUrl, it.userId)
            }

        }
    }
}

class GalleryInviteNotificationViewHolder(
    parent: ViewGroup,
    onClicked: () -> Unit
) : GalleryViewHolder(inflate(parent, ListItemInviteNotificationBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteNotificationBinding

    init {
        onClick(binding.lInviteNotification) { _ -> onClicked() }
    }

    override fun bind(data: GalleryListItem) {
        if (data !is GalleryInvitesNotificationListItem) return
        binding.tvInvitesMessage.text = TextFormatUtils.getFormattedInvitesKnocksMessage(
            context,
            data.invitesCount,
            data.knocksCount
        )
    }
}

class GalleryHeaderViewHolder(
    parent: ViewGroup,
) : GalleryViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: GalleryListItem) {
        if (data !is GalleryHeaderItem) return
        binding.tvHeader.text = context.getString(data.titleRes)
    }
}