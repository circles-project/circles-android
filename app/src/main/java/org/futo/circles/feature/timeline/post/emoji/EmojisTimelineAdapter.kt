package org.futo.circles.feature.timeline.post.emoji


import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.ReactionsData


class EmojisTimelineAdapter(
    private val onEmojiSelected: (ReactionsData) -> Unit
) : BaseRvAdapter<ReactionsData, EmojisTimelineViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        EmojisTimelineViewHolder(
            parent,
            onEmojiClicked = { position -> onEmojiSelected(getItem(position)) })


    override fun onBindViewHolder(holder: EmojisTimelineViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}