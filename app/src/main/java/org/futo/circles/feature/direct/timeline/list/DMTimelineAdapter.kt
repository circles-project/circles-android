package org.futo.circles.feature.direct.timeline.list

import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.DmTimelineItemPayload
import org.futo.circles.core.model.DmTimelineListItem
import org.futo.circles.core.model.DmTimelineLoadingItem
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.core.model.DmTimelineTimeHeaderItem
import org.futo.circles.core.model.PostContentType
import org.futo.circles.feature.direct.timeline.list.holder.DmDateHeaderViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmImageMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmNotMessageEventViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmTextMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmTimelineListItemViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmTimelineLoadingViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmVideoMessageViewHolder
import org.futo.circles.feature.direct.timeline.list.holder.DmViewHolder
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.list.OnVideoPlayBackStateListener
import org.futo.circles.feature.timeline.list.holder.VideoPlaybackViewHolder
import org.matrix.android.sdk.api.extensions.tryOrNull

private enum class DmTimelineViewType {
    TEXT, IMAGE, VIDEO, OTHER, LOADING, HEADER
}

class DMTimelineAdapter(
    private val dmOptionsListener: DmOptionsListener,
    private val videoPlayer: ExoPlayer
) : BaseRvAdapter<DmTimelineListItem, DmTimelineListItemViewHolder>(
    PayloadIdEntityCallback { old, new ->
        if (new is DmTimelineMessage && old is DmTimelineMessage)
            DmTimelineItemPayload(
                reactions = new.reactionsData,
                needToUpdateFullItem = new.content != old.content || new.info != old.info
            )
        else null
    }), OnVideoPlayBackStateListener {

    private var currentPlayingVideoHolder: VideoPlaybackViewHolder? = null

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()

    override fun getItemViewType(position: Int): Int = when (val item = getItem(position)) {
        is DmTimelineMessage -> {
            when (item.content.type) {
                PostContentType.TEXT_CONTENT -> DmTimelineViewType.TEXT.ordinal
                PostContentType.IMAGE_CONTENT -> DmTimelineViewType.IMAGE.ordinal
                PostContentType.VIDEO_CONTENT -> DmTimelineViewType.VIDEO.ordinal
                else -> DmTimelineViewType.OTHER.ordinal
            }
        }

        is DmTimelineTimeHeaderItem -> DmTimelineViewType.HEADER.ordinal
        is DmTimelineLoadingItem -> DmTimelineViewType.LOADING.ordinal
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DmTimelineListItemViewHolder =
        when (DmTimelineViewType.entries[viewType]) {
            DmTimelineViewType.TEXT -> DmTextMessageViewHolder(parent, dmOptionsListener)
            DmTimelineViewType.IMAGE -> DmImageMessageViewHolder(parent, dmOptionsListener)
            DmTimelineViewType.VIDEO -> DmVideoMessageViewHolder(
                parent,
                dmOptionsListener,
                videoPlayer,
                this
            )

            DmTimelineViewType.HEADER -> DmDateHeaderViewHolder(parent)
            DmTimelineViewType.OTHER -> DmNotMessageEventViewHolder(parent)
            DmTimelineViewType.LOADING -> DmTimelineLoadingViewHolder(parent)
        }


    override fun onBindViewHolder(holder: DmTimelineListItemViewHolder, position: Int) {
        val previousItem = tryOrNull { getItem(position - 1) }
        val nextItem = tryOrNull { getItem(position + 1) }
        holder.bind(getItem(position), previousItem, nextItem)
    }

    override fun onBindViewHolder(
        holder: DmTimelineListItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        (holder as? DmViewHolder) ?: run {
            super.onBindViewHolder(holder, position, payloads)
            return
        }

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                val payload = (it as? DmTimelineItemPayload) ?: return@forEach
                if (payload.needToUpdateFullItem) super.onBindViewHolder(holder, position, payloads)
                else holder.bindPayload(payload)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: DmTimelineListItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        if (holder == currentPlayingVideoHolder) stopVideoPlayback()
    }

    override fun onVideoPlaybackStateChanged(holder: VideoPlaybackViewHolder, isPlaying: Boolean) {
        currentPlayingVideoHolder = if (isPlaying) {
            stopVideoPlayback(false)
            holder
        } else null
    }

    fun stopVideoPlayback(shouldNotify: Boolean = true) {
        currentPlayingVideoHolder?.stopVideo(shouldNotify)
    }

}