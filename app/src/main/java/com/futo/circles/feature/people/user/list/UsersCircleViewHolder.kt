package com.futo.circles.feature.people.user.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.databinding.AcceptCircleInviteListItemBinding
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.model.JoinedCircleListItem

class UsersCircleViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(inflate(parent, AcceptCircleInviteListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as AcceptCircleInviteListItemBinding


    fun bind(data: JoinedCircleListItem) {
        with(binding) {
            tvCircleName.text = data.info.title
            binding.ivCircleImage.loadProfileIcon(data.info.avatarUrl, data.info.title)
        }
    }
}