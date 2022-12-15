package org.futo.circles.feature.log_in.switch_user.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ListItemSwitchUserBinding
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.extensions.onClick
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.model.SwitchUserListItem

class SwitchUsersViewHolder(
    parent: ViewGroup,
    onUserClicked: (Int) -> Unit,
    private val onRemoveClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemSwitchUserBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemSwitchUserBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
        onClick(binding.ivRemove) { position -> onRemoveClicked(position) }
    }

    fun bind(data: SwitchUserListItem) {
        with(binding) {
            ivUserImage.loadProfileIcon(
                data.user.avatarUrl,
                data.user.notEmptyDisplayName(),
                session = data.session
            )
            tvUserName.text = data.user.notEmptyDisplayName()
            tvUserId.text = data.user.userId
        }
    }
}