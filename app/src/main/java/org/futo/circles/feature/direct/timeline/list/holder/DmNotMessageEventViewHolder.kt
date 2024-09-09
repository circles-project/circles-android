package org.futo.circles.feature.direct.timeline.list.holder

import android.text.format.DateFormat
import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.OtherEventContent
import org.futo.circles.databinding.ListItemDmNotMessageEventBinding
import org.futo.circles.model.DmTimelineListItem
import org.futo.circles.model.DmTimelineMessage
import java.util.Date


class DmNotMessageEventViewHolder(
    parent: ViewGroup
) : DmTimelineListItemViewHolder(
    inflate(parent, ListItemDmNotMessageEventBinding::inflate)
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemDmNotMessageEventBinding

    override fun bind(item: DmTimelineListItem) {
        val dmMessage = (item as? DmTimelineMessage) ?: return
        val content = (dmMessage.content as? OtherEventContent) ?: return
        binding.tvMessage.text = content.eventType
        binding.tvTime.text =
            DateFormat.format("h:mm a", Date(dmMessage.info.getLastModifiedTimestamp()))
    }

}