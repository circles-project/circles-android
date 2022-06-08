package com.futo.circles.feature.photos.save.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.databinding.SelectGalleryListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.model.SelectableRoomListItem

class SelectGalleryViewHolder(
    parent: ViewGroup,
    onGalleryClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, SelectGalleryListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as SelectGalleryListItemBinding

    init {
        onClick(binding.baseGalleryItem.root) { position -> onGalleryClicked(position) }
    }

    fun bind(data: SelectableRoomListItem) {
        with(binding) {
            baseGalleryItem.ivGalleryImage.loadProfileIcon(data.info.avatarUrl, "")
            baseGalleryItem.tvGalleryName.text = data.info.title
            ivSelect.setImageResource(if (data.isSelected) R.drawable.ic_check_circle else R.drawable.ic_unselected)
            vSelectBackground.setIsVisible(data.isSelected)
        }
    }
}