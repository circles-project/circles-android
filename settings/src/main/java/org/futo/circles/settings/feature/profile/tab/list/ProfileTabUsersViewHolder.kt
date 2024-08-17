package org.futo.circles.settings.feature.profile.tab.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.settings.databinding.ListItemProfileTabUserBinding


class ProfileTabUsersViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemProfileTabUserBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemProfileTabUserBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    fun bind(user: CirclesUserSummary) {
        with(binding.lUser) {
            tvUserName.text = user.name
            tvUserId.text = user.id
            ivUserImage.loadUserProfileIcon(user.avatarUrl, user.id)
        }
    }
}
