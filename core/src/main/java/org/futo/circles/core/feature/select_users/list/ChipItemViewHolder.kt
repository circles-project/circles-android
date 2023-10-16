package org.futo.circles.core.feature.select_users.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.databinding.ListItemChipBinding
import org.futo.circles.core.base.list.ViewBindingHolder

class ChipItemViewHolder(
    parent: ViewGroup,
    private val onItemDeselected: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemChipBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemChipBinding

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