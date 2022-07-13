package org.futo.circles.core.picker.device.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.DeviceVideoListItem

class DeviceMedialListAdapter(
    private val onClick: (DeviceVideoListItem) -> Unit
) : BaseRvAdapter<DeviceVideoListItem, DeviceMediaViewHolder>(DefaultIdEntityCallback()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceMediaViewHolder =
        DeviceMediaViewHolder(
            parent,
        ) { position -> onClick(getItem(position)) }


    override fun onBindViewHolder(holder: DeviceMediaViewHolder, position: Int) =
        holder.bind(getItem(position))


}