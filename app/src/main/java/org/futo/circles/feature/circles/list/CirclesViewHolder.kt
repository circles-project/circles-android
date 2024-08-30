package org.futo.circles.feature.circles.list

import android.content.res.ColorStateList
import android.text.format.DateUtils
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.ColorUtils
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.base.list.context
import org.futo.circles.core.databinding.ListItemCircleAllPostsBinding
import org.futo.circles.core.databinding.ListItemInviteNotificationBinding
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadRoomProfileIcon
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.textDrawable.ColorGenerator
import org.futo.circles.core.model.CirclesUserSummary
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.TextFormatUtils
import org.futo.circles.databinding.ListItemCirclesHeaderBinding
import org.futo.circles.databinding.ListItemJoinedCircleBinding
import org.futo.circles.model.CircleInvitesNotificationListItem
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.CircleListItemPayload
import org.futo.circles.model.CirclesHeaderItem
import org.futo.circles.model.JoinedCircleListItem

abstract class CirclesViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: CircleListItem)

}


class JoinedCircleViewHolder(
    parent: ViewGroup,
    onCircleClicked: (Int) -> Unit
) : CirclesViewHolder(inflate(parent, ListItemJoinedCircleBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemJoinedCircleBinding

    init {
        onClick(itemView) { position -> onCircleClicked(position) }
    }

    override fun bind(data: CircleListItem) {
        if (data !is JoinedCircleListItem) return

        with(binding) {
            ivCircle.loadRoomProfileIcon(data.info.avatarUrl, data.info.title)
            binding.tvCircleTitle.text = data.info.title
            setOwnerIndicator(data.owner)
            setFollowersCount(data.followersCount, data.knockRequestsCount)
            setUpdateTime(data.timestamp)
            setUnreadCount(data.unreadCount)
        }
    }

    fun bindPayload(data: CircleListItemPayload) {
        data.followersCount?.let { setFollowersCount(it, data.knocksCount ?: 0) }
        data.knocksCount?.let { setFollowersCount(data.followersCount ?: 0, it) }
        data.unreadCount?.let { setUnreadCount(it) }
        data.timestamp?.let { setUpdateTime(it) }
    }


    private fun setOwnerIndicator(owner: CirclesUserSummary?) {
        owner ?: run { binding.chOwnerIndicator.gone(); return }
        with(binding.chOwnerIndicator) {
            setIsVisible(owner.id != MatrixSessionProvider.currentSession?.myUserId)
            text = owner.name
            val textColor = ColorGenerator().getColor(owner.id)
            setTextColor(textColor)
            chipStrokeColor =
                ColorStateList.valueOf(
                    ColorUtils.setAlphaComponent(
                        textColor,
                        (255 * 0.3).toInt()
                    )
                )
            chipBackgroundColor =
                ColorStateList.valueOf(
                    ColorUtils.setAlphaComponent(
                        textColor,
                        (255 * 0.1).toInt()
                    )
                )
        }
    }

    private fun setFollowersCount(followersCount: Int, knocksCount: Int) {
        val members = context.resources.getQuantityString(
            R.plurals.followers_plurals,
            followersCount, followersCount
        )
        val knocks = if (knocksCount > 0) " / ${
            context.resources.getQuantityString(
                R.plurals.request_plurals,
                knocksCount, knocksCount
            )
        }" else ""

        val membersInfo = members + knocks
        binding.tvFollowers.text = membersInfo
    }

    private fun setUpdateTime(timestamp: Long) {
        binding.tvUpdateTime.text = context.getString(
            org.futo.circles.core.R.string.last_updated_formatter,
            DateUtils.getRelativeTimeSpanString(
                timestamp, System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS
            )
        )
    }

    private fun setUnreadCount(count: Int) {
        binding.vNotificationsCount.setCount(count)
    }
}

class CircleInviteNotificationViewHolder(
    parent: ViewGroup,
    onClicked: () -> Unit
) : CirclesViewHolder(inflate(parent, ListItemInviteNotificationBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemInviteNotificationBinding

    init {
        onClick(binding.lInviteNotification) { _ -> onClicked() }
    }

    override fun bind(data: CircleListItem) {
        if (data !is CircleInvitesNotificationListItem) return
        binding.tvInvitesMessage.text = TextFormatUtils.getFormattedInvitesKnocksMessage(
            context,
            data.invitesCount,
            data.knocksCount
        )
    }
}


class CircleHeaderViewHolder(
    parent: ViewGroup,
) : CirclesViewHolder(inflate(parent, ListItemCirclesHeaderBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemCirclesHeaderBinding

    override fun bind(data: CircleListItem) {
        if (data !is CirclesHeaderItem) return
        binding.tvHeader.text = context.getString(data.titleRes)
    }
}

class CircleAllPostsViewHolder(
    parent: ViewGroup,
    onAllPostsClicked: () -> Unit
) : CirclesViewHolder(inflate(parent, ListItemCircleAllPostsBinding::inflate)) {

    private companion object : ViewBindingHolder

    init {
        onClick(itemView) { onAllPostsClicked() }
    }

    override fun bind(data: CircleListItem) {
    }
}

