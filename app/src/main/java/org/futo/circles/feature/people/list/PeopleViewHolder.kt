package org.futo.circles.feature.people.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.*
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.extensions.onClick
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.*
import org.futo.circles.view.UserListItemView

abstract class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: PeopleListItem)
}

class PeopleSuggestionUserViewHolder(
    parent: ViewGroup,
    private val onFollow: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleSuggestionBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleSuggestionBinding

    init {
        onClick(binding.btnFollow) { position -> onFollow(position) }
    }

    override fun bind(data: PeopleListItem) {
        (data as? PeopleUserListItem)?.let { binding.userItem.bind(it.user) }
        val user = (data as? PeopleSuggestionUserListItem) ?: return
        binding.btnFollow.setIsVisible(user.profileRoomId != null)
    }
}

class PeopleIgnoredUserViewHolder(
    parent: ViewGroup,
    private val onUnIgnore: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleIgnoredBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleIgnoredBinding

    init {
        onClick(binding.btnUnIgnore) { position -> onUnIgnore(position) }
    }

    override fun bind(data: PeopleListItem) {
        (data as? PeopleUserListItem)?.let { binding.userItem.bind(it.user) }
    }
}

class PeopleFollowingUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleFollowingBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleFollowingBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    override fun bind(data: PeopleListItem) {
        (data as? PeopleUserListItem)?.let { binding.userItem.bind(it.user) }
    }
}

class PeopleRequestUserViewHolder(
    parent: ViewGroup,
    private val onRequestClicked: (Int, Boolean) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleRequestBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleRequestBinding

    init {
        onClick(binding.btnAccept) { position -> onRequestClicked(position, true) }
        onClick(binding.btnDecline) { position -> onRequestClicked(position, false) }
    }

    override fun bind(data: PeopleListItem) {
        val user = (data as? PeopleRequestUserListItem)?.user ?: return
        with(binding) {
            tvUserName.text = user.name
            ivUserImage.loadProfileIcon(user.avatarUrl, user.name)
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
