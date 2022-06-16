package org.futo.circles.core.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.databinding.ChipListItemBinding

class ChipItemViewHolder(
    parent: ViewGroup,
    private val onItemDeselected: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ChipListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ChipListItemBinding

    init {
        binding.chipView.setOnCloseIconClickListener {
            bindingAdapterPosition.takeIf { it != -1 }?.let {
                onItemDeselected(it)
            }
        }
    }

    fun bind(text: String) {
        binding.chipView.text = text
    }
}