package org.futo.circles.gallery.feature.backup.list

import android.text.format.Formatter.formatFileSize
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.extensions.onClick
import org.futo.circles.gallery.databinding.ListItemMediaFolderBinding
import org.futo.circles.gallery.model.MediaFolderListItem

class MediaFolderViewHolder(
    parent: ViewGroup,
    private val onCheckChanged: (Int, Boolean) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemMediaFolderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMediaFolderBinding

    init {
        onClick(itemView) { binding.svFolder.isChecked = !binding.svFolder.isChecked }
        binding.svFolder.setOnCheckedChangeListener { _, isSelected ->
            bindingAdapterPosition.takeIf { it != -1 }?.let { onCheckChanged(it, isSelected) }
        }
    }

    fun bind(data: MediaFolderListItem) {
        with(binding) {
            tvFolderName.text = data.displayName
            tvSize.text = formatFileSize(context, data.size)
            svFolder.isChecked = data.isSelected
        }
    }
}