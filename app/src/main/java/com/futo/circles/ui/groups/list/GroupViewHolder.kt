package com.futo.circles.ui.groups.list

import android.text.format.DateUtils
import android.view.ViewGroup
import com.futo.circles.R
import com.futo.circles.base.BaseRecyclerViewHolder
import com.futo.circles.base.context
import com.futo.circles.databinding.GroupListItemBinding
import com.futo.circles.extensions.loadMatrixThumbnail
import com.futo.circles.extensions.nameOrId
import com.futo.circles.extensions.onClick
import org.matrix.android.sdk.api.session.content.ContentUrlResolver
import org.matrix.android.sdk.api.session.room.model.RoomSummary

class GroupViewHolder(
    parent: ViewGroup,
    private val urlResolver: ContentUrlResolver?,
    onGroupClicked: (Int) -> Unit
) : BaseRecyclerViewHolder<RoomSummary, GroupListItemBinding>(
    parent,
    GroupListItemBinding::inflate
) {

    init {
        onClick(itemView) { position -> onGroupClicked(position) }
    }

    override fun bind(data: RoomSummary) {
        with(binding) {
            ivGroup.loadMatrixThumbnail(data.avatarUrl, urlResolver)

            ivLock.setImageResource(if (data.isEncrypted) R.drawable.ic_lock else R.drawable.ic_lock_open)

            tvGroupTitle.text = data.nameOrId()
            
            val membersCount = data.joinedMembersCount ?: 0
            tvMembers.text = context.resources.getQuantityString(
                R.plurals.member_plurals,
                membersCount, membersCount
            )

            tvTopic.text = context.getString(
                R.string.topic_formatter,
                data.topic.takeIf { it.isNotEmpty() } ?: context.getString(R.string.none)
            )

            data.latestPreviewableEvent?.root?.originServerTs?.let { time ->
                tvUpdateTime.text = context.getString(
                    R.string.last_updated_formatter, DateUtils.getRelativeTimeSpanString(
                        time,
                        System.currentTimeMillis(),
                        DateUtils.HOUR_IN_MILLIS
                    )
                )
            }
        }
    }
}