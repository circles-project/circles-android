package org.futo.circles.feature.timeline.list

import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.model.PostListItem
import org.futo.circles.core.model.TimelineLoadingItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.feature.timeline.list.holder.ImagePostViewHolder
import org.futo.circles.feature.timeline.list.holder.MediaViewHolder
import org.futo.circles.feature.timeline.list.holder.OtherEventPostViewHolder
import org.futo.circles.feature.timeline.list.holder.PollPostViewHolder
import org.futo.circles.feature.timeline.list.holder.PostListItemViewHolder
import org.futo.circles.feature.timeline.list.holder.PostViewHolder
import org.futo.circles.feature.timeline.list.holder.TextPostViewHolder
import org.futo.circles.feature.timeline.list.holder.TimelineLoadingViewHolder
import org.futo.circles.feature.timeline.list.holder.VideoPostViewHolder
import org.futo.circles.model.PostItemPayload

private enum class TimelineViewType { TEXT, IMAGE, VIDEO, POLL, OTHER, LOADING }

class TimelineAdapter(
    private val postOptionsListener: PostOptionsListener,
    private val isThread: Boolean,
    private val videoPlayer: ExoPlayer
) : BaseRvAdapter<PostListItem, PostListItemViewHolder>(PayloadIdEntityCallback { old, new ->
    if (new is Post && old is Post)
        PostItemPayload(
            sendState = new.sendState,
            readByCount = new.readByCount,
            repliesCount = new.repliesCount,
            reactions = new.reactionsData,
            needToUpdateFullItem = new.content != old.content || new.postInfo != old.postInfo
        )
    else null
}), OnVideoPlayBackStateListener {

    private var currentPlayingVideoHolder: VideoPostViewHolder? = null

    private val uploadMediaTracker =
        MatrixSessionProvider.getSessionOrThrow().contentUploadProgressTracker()

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostListItemViewHolder {
        return when (TimelineViewType.entries[viewType]) {

            TimelineViewType.TEXT -> TextPostViewHolder(
                parent, postOptionsListener, isThread
            )

            TimelineViewType.IMAGE -> ImagePostViewHolder(
                parent, postOptionsListener, uploadMediaTracker, isThread
            )

            TimelineViewType.VIDEO -> VideoPostViewHolder(
                parent,
                postOptionsListener,
                isThread,
                uploadMediaTracker,
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

    override fun onBindViewHolder(holder: PostListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: PostListItemViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        (holder as? PostViewHolder) ?: run {
            super.onBindViewHolder(holder, position, payloads)
            return
        }

        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                val payload = (it as? PostItemPayload) ?: return@forEach
                if (payload.needToUpdateFullItem) holder.bind(getItem(position))
                else holder.bindPayload(payload)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PostListItemViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as? MediaViewHolder)?.unTrackMediaLoading()
        if (holder == currentPlayingVideoHolder) stopVideoPlayback()
    }

    override fun onVideoPlaybackStateChanged(holder: VideoPostViewHolder, isPlaying: Boolean) {
        currentPlayingVideoHolder = if (isPlaying) {
            stopVideoPlayback(false)
            holder
        } else null
    }

    fun stopVideoPlayback(shouldNotify: Boolean = true) {
        currentPlayingVideoHolder?.stopVideo(shouldNotify)
    }

}