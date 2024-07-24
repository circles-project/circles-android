package org.futo.circles.feature.direct.timeline.list

import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.TimelineLoadingItem
import org.futo.circles.feature.direct.timeline.list.holder.DmMyImageMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmMyTextMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmMyVideoMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmNotMessageEventViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmOtherImageMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmOtherTextMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmOtherVideoMessageViewHolder
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.base.BaseTimelineAdapter
import org.futo.circles.feature.timeline.base.TimelineListItemViewHolder
import org.futo.circles.feature.timeline.list.holder.TimelineLoadingViewHolder

private enum class DmTimelineViewType {
    MY_TEXT, OTHER_TEXT,
    MY_IMAGE, OTHER_IMAGE,
    MY_VIDEO, OTHER_VIDEO,
    OTHER, LOADING
}

class DMTimelineAdapter(
    private val dmOptionsListener: DmOptionsListener,
    private val videoPlayer: ExoPlayer
) : BaseTimelineAdapter() {


    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is Post -> {
            val isMyMessage = item.isMyPost()
            when (item.content.type) {
                PostContentType.TEXT_CONTENT -> if (isMyMessage) DmTimelineViewType.MY_TEXT.ordinal
                else DmTimelineViewType.OTHER_TEXT.ordinal

                PostContentType.IMAGE_CONTENT -> if (isMyMessage) DmTimelineViewType.MY_IMAGE.ordinal
                else DmTimelineViewType.OTHER_IMAGE.ordinal

                PostContentType.VIDEO_CONTENT -> if (isMyMessage) DmTimelineViewType.MY_VIDEO.ordinal
                else DmTimelineViewType.OTHER_VIDEO.ordinal

                else -> DmTimelineViewType.OTHER.ordinal
            }
        }

        is TimelineLoadingItem -> DmTimelineViewType.LOADING.ordinal
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineListItemViewHolder {
        return when (DmTimelineViewType.entries[viewType]) {
            DmTimelineViewType.MY_TEXT -> DmMyTextMessageViewHolder(parent, dmOptionsListener)
            DmTimelineViewType.OTHER_TEXT -> DmOtherTextMessageViewHolder(parent, dmOptionsListener)
            DmTimelineViewType.MY_IMAGE -> DmMyImageMessageViewHolder(parent, dmOptionsListener)
            DmTimelineViewType.OTHER_IMAGE -> DmOtherImageMessageViewHolder(
                parent,
                dmOptionsListener
            )

            DmTimelineViewType.MY_VIDEO -> DmMyVideoMessageViewHolder(
                parent,
                dmOptionsListener,
                videoPlayer,
                this
            )

            DmTimelineViewType.OTHER_VIDEO -> DmOtherVideoMessageViewHolder(
                parent,
                dmOptionsListener,
                videoPlayer,
                this
            )

            DmTimelineViewType.OTHER -> DmNotMessageEventViewHolder(parent)
            DmTimelineViewType.LOADING -> TimelineLoadingViewHolder(parent)
        }
    }


}