package org.futo.circles.feature.people.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.databinding.ListItemPeopleBinding
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.extensions.onClick
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleUserListItem

abstract class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: PeopleListItem)
}

class PeopleUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit,
    private val onFollow: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
        onClick(binding.btnFollow) { position -> onFollow(position) }
    }

    override fun bind(data: PeopleListItem) {
        if (data !is PeopleUserListItem) return

        with(binding) {
            userItem.tvUserName.text = data.user.name
            userItem.tvUserId.text = data.id
            userItem.ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
            btnFollow.setIsVisible(data.canFollow())
        }
    }
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
