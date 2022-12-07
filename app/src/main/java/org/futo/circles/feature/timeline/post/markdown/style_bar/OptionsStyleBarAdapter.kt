package org.futo.circles.feature.timeline.post.markdown.style_bar

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.StyleBarListItem

class OptionsStyleBarAdapter(
    private val onOptionSelected: (Int) -> Unit
) : BaseRvAdapter<StyleBarListItem, StyleBarOptionViewHolder>(
    DefaultIdEntityCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StyleBarOptionViewHolder {
        return StyleBarOptionViewHolder(parent = parent) { onOptionSelected(getItem(it).id) }
    }

    override fun onBindViewHolder(holder: StyleBarOptionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}