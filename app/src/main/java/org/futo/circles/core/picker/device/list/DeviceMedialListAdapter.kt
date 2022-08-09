package org.futo.circles.core.picker.device.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.core.picker.MediaType
import org.futo.circles.model.DeviceMediaListItem

class DeviceMedialListAdapter(
    private val onClick: (DeviceMediaListItem) -> Unit
) : BaseRvAdapter<DeviceMediaListItem, DeviceMediaViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).type.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DeviceMediaViewHolder = when (MediaType.values()[viewType]) {
        MediaType.Image -> DeviceImageViewHolder(
            parent,
        ) { position -> onClick(getItem(position)) }

        MediaType.Video -> DeviceVideoViewHolder(
            parent,
        ) { position -> onClick(getItem(position)) }
    }

    override fun onBindViewHolder(holder: DeviceMediaViewHolder, position: Int) =
        holder.bind(getItem(position))

}