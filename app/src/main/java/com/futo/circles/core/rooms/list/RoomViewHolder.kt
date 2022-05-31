package com.futo.circles.core.rooms.list

import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.R
import com.futo.circles.core.list.ViewBindingHolder
import com.futo.circles.core.list.context
import com.futo.circles.databinding.*
import com.futo.circles.extensions.loadProfileIcon
import com.futo.circles.extensions.onClick
import com.futo.circles.extensions.setIsEncryptedIcon
import com.futo.circles.model.*

abstract class RoomViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: RoomListItem)

    protected fun setIcon(groupIcon: ImageView, avatarUrl: String?, title: String) {
        groupIcon.loadProfileIcon(avatarUrl, title)
    }

    protected fun setIsEncrypted(lockIcon: ImageView, isEncrypted: Boolean) {
        lockIcon.setIsEncryptedIcon(isEncrypted)
    }

    protected fun setTitle(titleView: TextView, title: String) {
        titleView.text = title
    }
}

class JoinedGroupViewHolder(
    parent: ViewGroup,
    onGroupClicked: (Int) -> Unit
) : RoomViewHolder(inflate(parent, JoinedGroupListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as JoinedGroupListItemBinding

    init {
        onClick(itemView) { position -> onGroupClicked(position) }
    }

    override fun bind(data: RoomListItem) {
        if (data !is JoinedGroupListItem) return

        setIcon(binding.ivGroup, data.info.avatarUrl, data.info.title)
        setIsEncrypted(binding.ivLock, data.isEncrypted)
        setTitle(binding.tvGroupTitle, data.info.title)
        setTopic(data.topic)
        setMembersCount(data.membersCount)
        setUpdateTime(data.timestamp)
    }

    fun bindPayload(data: GroupListItemPayload) {
        data.isEncrypted?.let { setIsEncrypted(binding.ivLock, it) }
        data.topic?.let { setTopic(it) }
        data.membersCount?.let { setMembersCount(it) }
        data.timestamp?.let { setUpdateTime(it) }
    }

    private fun setTopic(topic: String) {
        binding.tvTopic.text = context.getString(
            R.string.topic_formatter,
            topic.takeIf { it.isNotEmpty() } ?: context.getString(R.string.none)
        )
    }

    private fun setMembersCount(membersCount: Int) {
        binding.tvMembers.text = context.resources.getQuantityString(
            R.plurals.member_plurals,
            membersCount, membersCount
        )
    }

    private fun setUpdateTime(timestamp: Long) {
        binding.tvUpdateTime.text = context.getString(
            R.string.last_updated_formatter, DateUtils.getRelativeTimeSpanString(
                timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )
        )
    }
}

class InvitedGroupViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : RoomViewHolder(inflate(parent, InvitedGroupListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InvitedGroupListItemBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
    }

    override fun bind(data: RoomListItem) {
        if (data !is InvitedGroupListItem) return

        setIcon(binding.ivGroup, data.info.avatarUrl, data.info.title)
        setIsEncrypted(binding.ivLock, data.isEncrypted)
        setTitle(binding.tvGroupTitle, data.info.title)
        binding.tvInviterName.text = context.getString(R.string.invited_by_format, data.inviterName)
    }

}

class JoinedCircleViewHolder(
    parent: ViewGroup,
    onCircleClicked: (Int) -> Unit
) : RoomViewHolder(inflate(parent, JoinedCircleListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as JoinedCircleListItemBinding

    init {
        onClick(itemView) { position -> onCircleClicked(position) }
    }

    override fun bind(data: RoomListItem) {
        if (data !is JoinedCircleListItem) return

        with(binding) {
            setIcon(ivCircle, data.info.avatarUrl, data.info.title)
            setTitle(tvCircleTitle, data.info.title)
            setFollowingCount(data.followingCount)
            setFollowedByCount(data.followedByCount)
        }
    }

    fun bindPayload(data: CircleListItemPayload) {
        setFollowingCount(data.followersCount)
        setFollowedByCount(data.followedByCount)
    }

    private fun setFollowingCount(followersCount: Int) {
        binding.tvFollowing.text = context.getString(R.string.following_format, followersCount)
    }

    private fun setFollowedByCount(followedByCount: Int) {
        binding.tvFollowedBy.text = context.getString(R.string.followed_by_format, followedByCount)
    }
}

class InvitedCircleViewHolder(
    parent: ViewGroup,
    onInviteClicked: (Int, Boolean) -> Unit
) : RoomViewHolder(inflate(parent, InvitedCircleListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as InvitedCircleListItemBinding

    init {
        onClick(binding.btnAccept) { position -> onInviteClicked(position, true) }
        onClick(binding.btnDecline) { position -> onInviteClicked(position, false) }
    }

    override fun bind(data: RoomListItem) {
        if (data !is InvitedCircleListItem) return

        with(binding) {
            setIcon(ivCircle, data.info.avatarUrl, data.info.title)
            setTitle(tvCircleTitle, data.info.title)
            binding.tvInvitedBy.text =
                context.getString(R.string.invited_by_format, data.inviterName)
        }
    }
}

class GalleryViewHolder(
    parent: ViewGroup,
    onGalleryClicked: (Int) -> Unit
) : RoomViewHolder(inflate(parent, GalleryListItemBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as GalleryListItemBinding

    init {
        onClick(itemView) { position -> onGalleryClicked(position) }
    }

    override fun bind(data: RoomListItem) {
        if (data !is GalleryListItem) return

        with(binding) {
            setIcon(ivGalleryImage, data.info.avatarUrl, "")
            setTitle(tvGalleryName, data.info.title)
        }
    }
}