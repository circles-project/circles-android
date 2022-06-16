package org.futo.circles.feature.room.select_users.list.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.databinding.InviteHeaderListItemBinding
import org.futo.circles.databinding.NoResultsListItemBinding
import org.futo.circles.databinding.UserListItemBinding
import org.futo.circles.extensions.loadProfileIcon
import org.futo.circles.extensions.onClick
import org.futo.circles.extensions.setSelectableItemBackground
import org.futo.circles.model.UserListItem
import org.futo.circles.model.HeaderItem
import org.futo.circles.model.InviteMemberListItem
import org.futo.circles.model.NoResultsItem

abstract class InviteMemberViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: InviteMemberListItem)
}

class UserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit
) : InviteMemberViewHolder(inflate(parent, UserListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as UserListItemBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
    }

    override fun bind(data: InviteMemberListItem) {
        if (data !is UserListItem) return

        with(binding) {
            setIcon(data)
            tvUserName.text = data.user.name
            tvUserId.text = data.id
        }
    }

    private fun setIcon(data: UserListItem) {
        if (data.isSelected) {
            binding.ivUserImage.setImageResource(R.drawable.ic_check_circle)
            binding.lRoot.setBackgroundColor(context.getColor(R.color.highlight_color))
        } else {
            binding.ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
            binding.lRoot.setSelectableItemBackground()
        }
    }
}

class HeaderViewHolder(
    parent: ViewGroup,
) : InviteMemberViewHolder(inflate(parent, InviteHeaderListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InviteHeaderListItemBinding

    override fun bind(data: InviteMemberListItem) {
        if (data !is HeaderItem) return

        binding.tvHeader.text = context.getString(data.titleRes)
    }
}

class NoResultViewHolder(
    parent: ViewGroup,
) : InviteMemberViewHolder(inflate(parent, NoResultsListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as NoResultsListItemBinding

    override fun bind(data: InviteMemberListItem) {
        if (data !is NoResultsItem) return

        binding.tvMessage.text = context.getString(data.titleRes)
    }
}