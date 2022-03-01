package com.futo.circles.feature.group_invite.list

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.InviteMemberListItemBinding
import com.futo.circles.model.RoomMemberListItem

class InviteMemberViewHolder(
    parent: ViewGroup,
) : RecyclerView.ViewHolder(inflate(parent, InviteMemberListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InviteMemberListItemBinding


    fun bind(data: RoomMemberListItem) {
        binding.tvText.text = data.name + " / " + data.id
    }

}