package org.futo.circles.feature.people.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.*
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.extensions.onClick
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleRequestUserListItem
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.view.UserListItemView

abstract class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: PeopleListItem)
}

sealed class PeopleBaseUserViewHolder(view: View) : PeopleViewHolder(view) {

    abstract val userListItem: UserListItemView

    override fun bind(data: PeopleListItem) {
        (data as? PeopleUserListItem)?.let {
            userListItem.bind(it.user)
        }
    }
}

class PeopleSuggestionUserViewHolder(
    parent: ViewGroup,
    private val onFollow: (Int) -> Unit
) : PeopleBaseUserViewHolder(inflate(parent, ListItemPeopleSuggestionBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleSuggestionBinding
    override val userListItem: UserListItemView = binding.userItem

    init {
        onClick(binding.btnFollow) { position -> onFollow(position) }
    }
}

class PeopleIgnoredUserViewHolder(
    parent: ViewGroup,
    private val onUnIgnore: (Int) -> Unit
) : PeopleBaseUserViewHolder(inflate(parent, ListItemPeopleIgnoredBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleIgnoredBinding
    override val userListItem: UserListItemView = binding.userItem

    init {
        onClick(binding.btnUnIgnore) { position -> onUnIgnore(position) }
    }
}

class PeopleFollowingUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : PeopleBaseUserViewHolder(inflate(parent, ListItemPeopleFollowingBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleFollowingBinding
    override val userListItem: UserListItemView = binding.userItem

    init {
        onClick(itemView) { position -> onUserClicked(position) }
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
