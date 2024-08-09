package org.futo.circles.view.members_list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.databinding.ListItemMemberAvatarBinding


class MembersAvatarsViewHolder(
    parent: ViewGroup
) : RecyclerView.ViewHolder(inflate(parent, ListItemMemberAvatarBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMemberAvatarBinding

    fun bind(data: CirclesUserSummary) {
        binding.ivUser.loadUserProfileIcon(data.avatarUrl, data.id)
    }

}