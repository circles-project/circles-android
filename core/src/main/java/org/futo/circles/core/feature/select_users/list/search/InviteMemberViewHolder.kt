package org.futo.circles.core.feature.select_users.list.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.databinding.ListItemInviteHeaderBinding
import org.futo.circles.core.databinding.ListItemInviteMemberBinding
import org.futo.circles.core.databinding.ListItemNoResultsBinding
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.model.HeaderItem
import org.futo.circles.core.model.InviteMemberListItem
import org.futo.circles.core.model.NoResultsItem
import org.futo.circles.core.model.UserListItem

abstract class InviteMemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: InviteMemberListItem)
}

class UserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : InviteMemberViewHolder(inflate(parent, ListItemInviteMemberBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteMemberBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    override fun bind(data: InviteMemberListItem) {
        if (data !is UserListItem) return
        binding.vUser.bindSelectable(data.user, data.isSelected)
    }
}

class HeaderViewHolder(
    parent: ViewGroup,
) : InviteMemberViewHolder(inflate(parent, ListItemInviteHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteHeaderBinding

    override fun bind(data: InviteMemberListItem) {
        if (data !is HeaderItem) return

        binding.tvHeader.text = context.getString(data.titleRes)
    }
}

class NoResultViewHolder(
    parent: ViewGroup,
) : InviteMemberViewHolder(inflate(parent, ListItemNoResultsBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemNoResultsBinding

    override fun bind(data: InviteMemberListItem) {
        if (data !is NoResultsItem) return

        binding.tvMessage.text = context.getString(data.titleRes)
    }
}