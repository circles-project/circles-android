package org.futo.circles.feature.timeline.post.emoji.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.EmojiItem

class EmojiAdapter(
    private val onEmojiSelected: (item: EmojiItem) -> Unit
) : BaseRvAdapter<EmojiItem, EmojiViewHolder>(DefaultIdEntityCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): EmojiViewHolder = EmojiViewHolder(
        parent = parent,
        onEmojiSelected = { position -> onEmojiSelected(getItem(position)) }
    )

    override fun onBindViewHolder(holder: EmojiViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}