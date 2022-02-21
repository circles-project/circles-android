package com.futo.circles.ui.groups.timeline.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRecyclerViewHolder
import com.futo.circles.databinding.GroupTimelineListItemBinding
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class GroupTimelineViewHolder(
    parent: ViewGroup
) : BaseRecyclerViewHolder<TimelineEvent, GroupTimelineListItemBinding>(
    parent,
    GroupTimelineListItemBinding::inflate
) {

    override fun bind(event: TimelineEvent) {
        with(binding) {
            tvMessage.text = when (event.root.getClearType()) {
                EventType.MESSAGE -> formatMessage(event)
                else -> "Event of type ${event.root.getClearType()} not rendered yet"
            }
        }
    }

    private fun formatMessage(timelineEvent: TimelineEvent): String {
        val messageContent =
            timelineEvent.root.getClearContent().toModel<MessageContent>() ?: return ""
        return messageContent.body
    }
}