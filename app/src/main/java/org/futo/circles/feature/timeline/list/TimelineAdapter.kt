package org.futo.circles.feature.timeline.list

import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.Post
import org.futo.circles.model.PostContentType
import org.futo.circles.model.PostItemPayload
import org.futo.circles.model.RootPost
import org.futo.circles.view.PostOptionsListener

class TimelineAdapter(
    private val userPowerLevel: Int,
    private val postOptionsListener: PostOptionsListener,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<Post, PostViewHolder>(PayloadIdEntityCallback { old, new ->
    (new as? RootPost)?.let { rootPost ->
        PostItemPayload(
            repliesCount = rootPost.getRepliesCount(),
            isRepliesVisible = rootPost.isRepliesVisible,
            hasReplies = rootPost.hasReplies(),
            needToUpdateFullItem = rootPost.content != old.content || rootPost.postInfo != old.postInfo
        )
    }
}) {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).content.type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (PostContentType.values()[viewType]) {
            PostContentType.TEXT_CONTENT -> TextPostViewHolder(
                parent, postOptionsListener, userPowerLevel
            )
            PostContentType.IMAGE_CONTENT -> ImagePostViewHolder(
                parent, postOptionsListener, userPowerLevel
            )
            PostContentType.VIDEO_CONTENT -> VideoPostViewHolder(
                parent, postOptionsListener, userPowerLevel
            )
            PostContentType.POLL_CONTENT -> PollPostViewHolder(
                parent, postOptionsListener, userPowerLevel
            )
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
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
                (it as? PostItemPayload)?.let { payload ->
                    if (payload.needToUpdateFullItem)
                        holder.bind(getItem(position))
                    else
                        holder.bindPayload(payload)
                }
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PostViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as? UploadMediaViewHolder)?.uploadMediaTracker?.unTrack()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

}