package org.futo.circles.settings.feature.profile.tab.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemDefaultUserBinding
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.settings.model.PeopleHeaderItem
import org.futo.circles.settings.model.PeopleListItem
import org.futo.circles.settings.model.PeopleUserListItem

abstract class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: PeopleListItem)
}

class PeopleHeaderViewHolder(
    parent: ViewGroup,
) : PeopleViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: PeopleListItem) {
        if (data !is PeopleHeaderItem) return
        binding.tvHeader.text = context.getString(data.titleRes)
    }
}


class PeopleUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemDefaultUserBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemDefaultUserBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    override fun bind(data: PeopleListItem) {
        val userItem = (data as? PeopleUserListItem) ?: return
        with(binding.lUser) {
            tvUserName.text = userItem.user.name
            tvUserId.text = userItem.user.id
            ivUserImage.loadUserProfileIcon(userItem.user.avatarUrl, userItem.user.id)
        }
    }
}
