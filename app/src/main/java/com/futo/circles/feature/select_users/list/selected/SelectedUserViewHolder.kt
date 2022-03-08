package com.futo.circles.feature.select_users.list.selected

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.SelectedUserChipItemBinding
import com.futo.circles.model.UserListItem

class SelectedUserViewHolder(
    parent: ViewGroup,
    private val onUserDeselected: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, SelectedUserChipItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as SelectedUserChipItemBinding

    init {
        binding.userChip.setOnCloseIconClickListener {
            bindingAdapterPosition.takeIf { it != -1 }?.let {
                onUserDeselected(it)
            }
        }
    }

    fun bind(data: UserListItem) {
        binding.userChip.text = data.user.name
    }
}