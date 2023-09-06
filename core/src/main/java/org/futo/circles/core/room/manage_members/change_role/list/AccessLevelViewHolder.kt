package org.futo.circles.core.room.manage_members.change_role.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.extensions.getRoleNameResId
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemAccessLevelBinding
import org.futo.circles.model.AccessLevelListItem

class AccessLevelViewHolder(
    parent: ViewGroup,
    onRoleClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemAccessLevelBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemAccessLevelBinding

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