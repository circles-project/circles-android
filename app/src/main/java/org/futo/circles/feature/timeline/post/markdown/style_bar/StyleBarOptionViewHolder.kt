package org.futo.circles.feature.timeline.post.markdown.style_bar

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemStyleBarBinding
import org.futo.circles.model.StyleBarListItem

class StyleBarOptionViewHolder(
    parent: ViewGroup, onItemClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(
    inflate(parent, ListItemStyleBarBinding::inflate)
) {
    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemStyleBarBinding

    init {
        onClick(binding.cvOption) { onItemClicked(it) }
    }

    fun bind(data: StyleBarListItem) {
        binding.ivIcon.setImageResource(data.iconResId)
        binding.cvOption.setCardBackgroundColor(
            ContextCompat.getColor(
                context, if (data.isSelected) R.color.blue else R.color.post_card_background_color
            )
        )
    }
}

