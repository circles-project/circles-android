package com.futo.circles.feature.manage_group_members.change_role.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.databinding.AccessLevelListItemBinding
import com.futo.circles.extensions.getRoleNameResId
import com.futo.circles.extensions.onClick
import com.futo.circles.model.AccessLevelListItem

class AccessLevelViewHolder(
    parent: ViewGroup,
    onRoleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, AccessLevelListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as AccessLevelListItemBinding

    init {
        onClick(itemView) { position -> onRoleClicked(position) }
    }

    fun bind(data: AccessLevelListItem) {
        with(binding.rbAccessLevel) {
            isChecked = data.isSelected
            text = context.getString(data.role.getRoleNameResId())
        }
    }
}