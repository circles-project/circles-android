package org.futo.circles.gallery.feature.select.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.loadMatrixImage
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.feature.textDrawable.TextDrawable
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.gallery.databinding.ListItemSelectGalleryBinding

class SelectGalleryViewHolder(
    parent: ViewGroup,
    onGalleryClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemSelectGalleryBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSelectGalleryBinding

    init {
        onClick(binding.baseGalleryItem.root) { position -> onGalleryClicked(position) }
    }

    fun bind(data: SelectableRoomListItem) {
        with(binding) {
            val placeholder = TextDrawable.Builder()
                .setShape(TextDrawable.SHAPE_ROUND_RECT)
                .setColor(ColorGenerator().getColor(data.id))
                .build()
            baseGalleryItem.ivGalleryImage.loadMatrixImage(
                url = data.info.avatarUrl,
                placeholder = placeholder
            )
            baseGalleryItem.tvGalleryName.text = data.info.title
            ivSelect.setImageResource(
                if (data.isSelected) org.futo.circles.core.R.drawable.ic_check_circle
                else org.futo.circles.core.R.drawable.ic_unselected
            )
        }
    }
}