package org.futo.circles.feature.photos.backup.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemMediaFolderBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.MediaFolderListItem
import kotlin.math.log10
import kotlin.math.pow

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
            tvSize.text = formatFileSize(data.size)
            svFolder.isChecked = data.isSelected
        }
    }

    private fun formatFileSize(size: Long): String {
        if (size <= 0) return "0 B"
        val units = arrayOf("B", "KB", "MB", "GB", "TB")
        val digitGroups = (log10(size.toDouble()) / log10(1024.0)).toInt()
        return String.format(
            "%.1f %s", size / 1024.0.pow(digitGroups.toDouble()), units[digitGroups]
        )
    }
}