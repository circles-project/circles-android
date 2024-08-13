package org.futo.circles.feature.timeline.base

import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostListItem
import org.futo.circles.feature.timeline.list.OnVideoPlayBackStateListener
import org.futo.circles.feature.timeline.list.holder.PostViewHolder
import org.futo.circles.feature.timeline.list.holder.VideoPlaybackViewHolder
import org.futo.circles.model.PostItemPayload

abstract class BaseTimelineAdapter : BaseRvAdapter<PostListItem, TimelineListItemViewHolder>(
    PayloadIdEntityCallback { old, new ->
        if (new is Post && old is Post)
            PostItemPayload(
                repliesCount = new.repliesCount,
                reactions = new.reactionsData,
                needToUpdateFullItem = new.content != old.content || new.postInfo != old.postInfo
            )
        else null
    }), OnVideoPlayBackStateListener {

    private var currentPlayingVideoHolder: VideoPlaybackViewHolder? = null

    override fun getItemId(position: Int): Long = getItem(position).id.hashCode().toLong()

    override fun onBindViewHolder(holder: TimelineListItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun onBindViewHolder(
        holder: TimelineListItemViewHolder,
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

    override fun onViewDetachedFromWindow(holder: TimelineListItemViewHolder) {
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