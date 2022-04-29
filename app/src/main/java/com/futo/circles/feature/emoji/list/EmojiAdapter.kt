package com.futo.circles.feature.emoji.list

import android.view.ViewGroup
import com.futo.circles.core.list.BaseRvAdapter
import com.futo.circles.model.EmojiItem

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