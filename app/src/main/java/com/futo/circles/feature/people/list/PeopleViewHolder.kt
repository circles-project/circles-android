package com.futo.circles.feature.people.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.InviteHeaderListItemBinding
import com.futo.circles.databinding.PeopleListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.model.PeopleHeaderItem
import com.futo.circles.model.PeopleListItem
import com.futo.circles.model.PeopleUserListItem

abstract class PeopleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: PeopleListItem)
}

class PeopleUserViewHolder(
    parent: ViewGroup,
    private val onUserClicked: (Int) -> Unit,
    private val onIgnore: (Int, Boolean) -> Unit
) : PeopleViewHolder(inflate(parent, PeopleListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as PeopleListItemBinding

    init {
        onClick(itemView) { position -> onUserClicked(position) }
        onClick(binding.btnIgnore) { position -> onIgnore(position, true) }
        onClick(binding.btnUnignore) { position -> onIgnore(position, false) }
    }

    override fun bind(data: PeopleListItem) {
        if (data !is PeopleUserListItem) return

        with(binding) {
            userItem.tvUserName.text = data.user.name
            userItem.tvUserId.text = data.id
            userItem.ivUserImage.loadProfileIcon(data.user.avatarUrl, data.user.name)
            btnIgnore.setIsVisible(!data.isIgnored)
            btnUnignore.setIsVisible(data.isIgnored)
        }
    }
}

class PeopleHeaderViewHolder(
    parent: ViewGroup,
) : PeopleViewHolder(inflate(parent, InviteHeaderListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InviteHeaderListItemBinding

    override fun bind(data: PeopleListItem) {
        if (data !is PeopleHeaderItem) return

        binding.tvHeader.text = context.getString(data.titleRes)
    }
}
