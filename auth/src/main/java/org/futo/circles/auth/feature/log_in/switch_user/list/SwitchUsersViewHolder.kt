package org.futo.circles.auth.feature.log_in.switch_user.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.auth.databinding.ListItemSwitchUserBinding
import org.futo.circles.auth.model.SwitchUserListItem
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.onClick

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
            ivUserImage.loadUserProfileIcon(
                data.user.avatarUrl,
                data.user.userId,
                session = data.session
            )
            tvUserName.text = data.user.notEmptyDisplayName()
            tvUserId.text = data.user.userId
        }
    }
}