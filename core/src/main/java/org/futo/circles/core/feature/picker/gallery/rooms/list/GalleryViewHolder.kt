package org.futo.circles.core.feature.picker.gallery.rooms.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemInvitedGalleryBinding
import org.futo.circles.core.databinding.ListItemJoinedGalleryBinding
import org.futo.circles.core.extensions.loadMatrixImage
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.feature.textDrawable.TextDrawable
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.InvitedGalleryListItem
import org.futo.circles.core.model.JoinedGalleryListItem


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
        }
    }
}

class InvitedGalleryViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit,
    onShowProfileIconClicked: (Int) -> Unit
) : GalleryViewHolder(inflate(parent, ListItemInvitedGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInvitedGalleryBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
        onClick(binding.ivGallery) { position -> onShowProfileIconClicked(position) }
    }

    override fun bind(data: GalleryListItem) {
        if (data !is InvitedGalleryListItem) return

        with(binding) {
            tvGalleryTitle.text = data.info.title
            ivGallery.loadProfileIcon(
                data.info.avatarUrl,
                data.info.title,
                applyBlur = data.shouldBlurIcon
            )
            tvShowProfileImage.setIsVisible(data.shouldBlurIcon)
            tvInviterName.text = context.getString(R.string.invited_by_format, data.inviterName)
        }
    }
}