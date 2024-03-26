package org.futo.circles.feature.timeline.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.feature.timeline.data_source.BaseTimelineDataSource
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContentType
import org.futo.circles.feature.timeline.list.holder.ImagePostViewHolder
import org.futo.circles.feature.timeline.list.holder.PollPostViewHolder
import org.futo.circles.feature.timeline.list.holder.PostViewHolder
import org.futo.circles.feature.timeline.list.holder.TextPostViewHolder
import org.futo.circles.feature.timeline.list.holder.VideoPostViewHolder
import org.futo.circles.model.PostItemPayload

class TimelineAdapter(
    private val postOptionsListener: PostOptionsListener,
    private val isThread: Boolean,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<Post, PostViewHolder>(PayloadIdEntityCallback { old, new ->
    PostItemPayload(
        sendState = new.sendState,
        readByCount = new.readByCount,
        repliesCount = new.repliesCount,
        reactions = new.reactionsData,
        needToUpdateFullItem = new.content != old.content || new.postInfo != old.postInfo
    )
}) {

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()

    override fun getItemViewType(position: Int): Int = getItem(position).content.type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (PostContentType.entries[viewType]) {

            PostContentType.TEXT_CONTENT -> TextPostViewHolder(
                parent, postOptionsListener, isThread
            )

            PostContentType.IMAGE_CONTENT -> ImagePostViewHolder(
                parent, postOptionsListener, isThread
            )

            PostContentType.VIDEO_CONTENT -> VideoPostViewHolder(
                parent, postOptionsListener, isThread
            )

            PostContentType.POLL_CONTENT -> PollPostViewHolder(
                parent, postOptionsListener, isThread
            )
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - BaseTimelineDataSource.LOAD_MORE_THRESHOLD) onLoadMore()
    }

    override fun onBindViewHolder(
        holder: PostViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
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

    override fun onViewDetachedFromWindow(holder: PostViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as? UploadMediaViewHolder)?.uploadMediaTracker?.unTrack()
    }

}