package org.futo.circles.feature.timeline.post.emoji

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.ReactionsData
import org.futo.circles.databinding.ListItemTimelineReactionBinding

class EmojisTimelineViewHolder(
    parent: ViewGroup,
    private val onEmojiClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemTimelineReactionBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemTimelineReactionBinding

    init {
        onClick(binding.emojiChip) { position -> onEmojiClicked(position) }
    }

    fun bind(data: ReactionsData) {
        binding.emojiChip.apply {
            val title = "${data.key} ${data.count}"
            text = title
            isChecked = data.addedByMe
        }
    }
}