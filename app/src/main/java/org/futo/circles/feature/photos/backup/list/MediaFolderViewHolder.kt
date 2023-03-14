package org.futo.circles.feature.photos.backup.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemMediaFolderBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.MediaFolderListItem

class MediaFolderViewHolder(
    parent: ViewGroup,
    private val onCheckChanged: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemMediaFolderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMediaFolderBinding

    init {
        onClick(itemView) { binding.svFolder.isChecked = !binding.svFolder.isChecked }
        binding.svFolder.setOnCheckedChangeListener { _, _ ->
            bindingAdapterPosition.takeIf { it != -1 }?.let { onCheckChanged(it) }
        }
    }

    fun bind(data: MediaFolderListItem) {
        with(binding) {
            tvFolderName.text = data.displayName
            svFolder.isChecked = data.isSelected
        }
    }
}