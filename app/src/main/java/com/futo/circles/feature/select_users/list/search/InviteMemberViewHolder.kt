package com.futo.circles.feature.select_users.list.search

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.base.context
import com.futo.circles.databinding.InviteHeaderListItemBinding
import com.futo.circles.databinding.NoResultsListItemBinding
import com.futo.circles.databinding.UserListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setSelectableItemBackground
import com.futo.circles.model.UserListItem
import com.futo.circles.model.HeaderItem
import com.futo.circles.model.InviteMemberListItem
import com.futo.circles.model.NoResultsItem

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