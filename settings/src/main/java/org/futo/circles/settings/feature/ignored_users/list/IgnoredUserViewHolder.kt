package org.futo.circles.settings.feature.ignored_users.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.settings.databinding.ListItemIgnoredUserBinding

class IgnoredUserViewHolder(
    parent: ViewGroup,
    private val onUnIgnore: (Int) -> Unit
) : RecyclerView.ViewHolder(inflate(parent, ListItemIgnoredUserBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemIgnoredUserBinding

    init {
        onClick(binding.btnUnIgnore) { position -> onUnIgnore(position) }
    }

    fun bind(user: CirclesUserSummary) {
        with(binding) {
            tvUserName.text = user.name
            tvUserId.text = user.id
            ivUserImage.loadUserProfileIcon(user.avatarUrl, user.id)
        }
    }

}