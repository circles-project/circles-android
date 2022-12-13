package org.futo.circles.feature.log_in.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemSwitchUserBinding
import org.futo.circles.extensions.onClick
import org.futo.circles.model.SwitchUserListItem

class SwitchUsersViewHolder(
    parent: ViewGroup,
    onUserClicked: (Int) -> Unit,
    onRemoveClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemSwitchUserBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSwitchUserBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    fun bind(data: SwitchUserListItem) {

    }
}