package org.futo.circles.feature.people.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.ListItemInviteHeaderBinding
import org.futo.circles.databinding.ListItemPeopleDefaultBinding
import org.futo.circles.databinding.ListItemPeopleIgnoredBinding
import org.futo.circles.databinding.ListItemPeopleRequestBinding
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.model.PeopleHeaderItem
import org.futo.circles.model.PeopleListItem
import org.futo.circles.model.PeopleUserListItem
import org.futo.circles.model.PeopleUserListItemPayload

abstract class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: PeopleListItem)
    open fun bindPayload(data: PeopleUserListItemPayload) {}
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

    override fun bindPayload(data: PeopleUserListItemPayload) {
        data.user?.let { binding.userItem.bind(it) }
    }
}

class PeopleDefaultUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : PeopleViewHolder(inflate(parent, ListItemPeopleDefaultBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemPeopleDefaultBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    override fun bind(data: PeopleListItem) {
        (data as? PeopleUserListItem)?.let { binding.userItem.bind(it.user) }
    }

    override fun bindPayload(data: PeopleUserListItemPayload) {
        data.user?.let { binding.userItem.bind(it) }
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
        val user = (data as? PeopleUserListItem)?.user ?: return
        bindUser(user)
    }

    override fun bindPayload(data: PeopleUserListItemPayload) {
        data.user?.let { bindUser(it) }
    }

    private fun bindUser(user: CirclesUserSummary) {
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
