package org.futo.circles.core.feature.room.invites.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.InviteListItem
import org.futo.circles.core.model.InviteTypeArg


class InvitesAdapter(
    private val onInviteClicked: (InviteListItem, Boolean) -> Unit,
    private val onUnblurProfileIconClicked: (InviteListItem) -> Unit
) : BaseRvAdapter<InviteListItem, InviteViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = getItem(position).inviteType.ordinal

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (InviteTypeArg.entries[viewType]) {
        InviteTypeArg.Circle -> InvitedCircleViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            },
            onShowProfileIconClicked = { position ->
                onUnblurProfileIconClicked(getItem(position))
            })

        InviteTypeArg.Group -> InvitedGroupViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            },
            onShowProfileIconClicked = { position ->
                onUnblurProfileIconClicked(getItem(position))
            })

        InviteTypeArg.Photo -> InvitedGalleryViewHolder(
            parent = parent,
            onInviteClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            },
            onShowProfileIconClicked = { position ->
                onUnblurProfileIconClicked(getItem(position))
            })

        InviteTypeArg.People -> FollowRequestViewHolder(
            parent = parent,
            onRequestClicked = { position, isAccepted ->
                onInviteClicked(getItem(position), isAccepted)
            })
    }


    override fun onBindViewHolder(holder: InviteViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}