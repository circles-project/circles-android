package com.futo.circles.ui.groups.timeline.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.ui.groups.timeline.model.GroupImageMessage
import com.futo.circles.ui.groups.timeline.model.GroupMessage
import com.futo.circles.ui.groups.timeline.model.GroupMessageType
import com.futo.circles.ui.groups.timeline.model.GroupTextMessage
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

class GroupTimelineAdapter(
    private val urlResolver: ContentUrlResolver?,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<GroupMessage, GroupTimelineViewHolder>(DefaultDiffUtilCallback()) {

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is GroupTextMessage -> GroupMessageType.TEXT_MESSAGE.ordinal
            is GroupImageMessage -> GroupMessageType.IMAGE_MESSAGE.ordinal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupTimelineViewHolder {
        return when (GroupMessageType.values()[viewType]) {
            GroupMessageType.TEXT_MESSAGE -> TextMessageViewHolder(parent,urlResolver)
            GroupMessageType.IMAGE_MESSAGE -> ImageMessageViewHolder(parent, urlResolver)
        }
    }

    override fun onBindViewHolder(holder: GroupTimelineViewHolder, position: Int) {
        when(holder){
            is ImageMessageViewHolder -> holder.bind(getItemAs(position))
            is TextMessageViewHolder -> holder.bind(getItemAs(position))
        }
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

}