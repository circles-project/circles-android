package org.futo.circles.core.picker.device.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.DeviceVideoListItemBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.DeviceVideoListItem

class DeviceMediaViewHolder(
    parent: ViewGroup,
    onMediaClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, DeviceVideoListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as DeviceVideoListItemBinding

    init {
        onClick(itemView) { position -> onMediaClicked(position) }
    }

    fun bind(data: DeviceVideoListItem) {
        with(binding) {
            ivVideoCover.setImageBitmap(data.thumbnail)
            tvDuration.text = data.durationString
        }
    }
}