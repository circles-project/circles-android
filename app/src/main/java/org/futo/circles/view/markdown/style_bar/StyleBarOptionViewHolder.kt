package org.futo.circles.view.markdown.style_bar

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemStyleBarBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.StyleBarListItem

class StyleBarOptionViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(
    inflate(parent, ListItemStyleBarBinding::inflate)
) {
    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemStyleBarBinding

    init {
        onClick(itemView) { onItemClicked(it) }
    }

    fun bind(data: StyleBarListItem) {
        binding.btnOption.setIconResource(data.iconResId)
        binding.btnOption.isChecked = data.isSelected
    }
}

