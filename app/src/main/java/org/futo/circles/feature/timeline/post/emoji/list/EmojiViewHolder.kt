package org.futo.circles.feature.timeline.post.emoji.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.EmojiListItemBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.EmojiItem

class EmojiViewHolder(
    parent: ViewGroup,
    onEmojiSelected: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, EmojiListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as EmojiListItemBinding

    init {
        onClick(binding.tvEmoji) { position -> onEmojiSelected(position) }
    }

    fun bind(data: EmojiItem) {
        binding.tvEmoji.text = data.emoji
    }
}