package org.futo.circles.feature.timeline.list

import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.TimelineLoadingItem
import org.futo.circles.feature.timeline.base.BaseTimelineAdapter
import org.futo.circles.feature.timeline.base.TimelineListItemViewHolder
import org.futo.circles.feature.timeline.list.holder.ImagePostViewHolder
import org.futo.circles.feature.timeline.list.holder.OtherEventPostViewHolder
import org.futo.circles.feature.timeline.list.holder.PollPostViewHolder
import org.futo.circles.feature.timeline.list.holder.TextPostViewHolder
import org.futo.circles.feature.timeline.list.holder.TimelineLoadingViewHolder
import org.futo.circles.feature.timeline.list.holder.VideoPostViewHolder

private enum class TimelineViewType { TEXT, IMAGE, VIDEO, POLL, OTHER, LOADING }

class TimelineAdapter(
    private val postOptionsListener: PostOptionsListener,
    private val isThread: Boolean,
    private val videoPlayer: ExoPlayer
) : BaseTimelineAdapter() {


    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is Post -> when (item.content.type) {
            PostContentType.TEXT_CONTENT -> TimelineViewType.TEXT.ordinal
            PostContentType.IMAGE_CONTENT -> TimelineViewType.IMAGE.ordinal
            PostContentType.VIDEO_CONTENT -> TimelineViewType.VIDEO.ordinal
            PostContentType.POLL_CONTENT -> TimelineViewType.POLL.ordinal
            PostContentType.OTHER_CONTENT -> TimelineViewType.OTHER.ordinal
        }

        is TimelineLoadingItem -> TimelineViewType.LOADING.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimelineListItemViewHolder {
        return when (TimelineViewType.entries[viewType]) {

            TimelineViewType.TEXT -> TextPostViewHolder(
                parent, postOptionsListener, isThread
            )

            TimelineViewType.IMAGE -> ImagePostViewHolder(parent, postOptionsListener, isThread)

            TimelineViewType.VIDEO -> VideoPostViewHolder(
                parent,
                postOptionsListener,
                isThread,
                videoPlayer,
                this
            )

            TimelineViewType.POLL -> PollPostViewHolder(
                parent, postOptionsListener, isThread
            )

            TimelineViewType.OTHER -> OtherEventPostViewHolder(parent, postOptionsListener)
            TimelineViewType.LOADING -> TimelineLoadingViewHolder(parent)
        }
    }
}