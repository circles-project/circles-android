package org.futo.circles.feature.direct.timeline.list.holder

import android.text.format.DateFormat
import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.OtherEventContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostListItem
import org.futo.circles.databinding.ListItemDmNotMessageEventBinding
import org.futo.circles.feature.timeline.base.TimelineListItemViewHolder
import java.util.Date


class DmNotMessageEventViewHolder(
    parent: ViewGroup
) : TimelineListItemViewHolder(
    inflate(parent, ListItemDmNotMessageEventBinding::inflate)
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemDmNotMessageEventBinding

    override fun bind(item: PostListItem) {
        val post = (item as? Post) ?: return
        val content = (post.content as? OtherEventContent) ?: return
        binding.tvMessage.text = content.eventType
        binding.tvTime.text =
            DateFormat.format("MMM dd, h:mm a", Date(post.postInfo.getLastModifiedTimestamp()))
    }

}