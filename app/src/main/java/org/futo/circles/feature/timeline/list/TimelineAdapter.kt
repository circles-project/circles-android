package org.futo.circles.feature.timeline.list

import android.annotation.SuppressLint
import android.view.ViewGroup
import org.futo.circles.core.list.BaseRvAdapter
import org.futo.circles.model.Post
import org.futo.circles.model.PostContentType
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.PostOptionsListener

class TimelineAdapter(
    private var userPowerLevel: Int,
    private val postOptionsListener: PostOptionsListener,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<Post, PostViewHolder>(PayloadIdEntityCallback { old, new ->
    PostItemPayload(
        sendState = new.sendState,
        readInfo = new.readInfo,
        needToUpdateFullItem = new.content != old.content || new.postInfo != old.postInfo
    )
}) {
    @SuppressLint("NotifyDataSetChanged")
    fun updateUserPowerLevel(level: Int) {
        userPowerLevel = level
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).content.type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (PostContentType.values()[viewType]) {
            PostContentType.POLL_CONTENT -> PollPostViewHolder(parent, postOptionsListener)
            else -> TextMediaPostViewHolder(parent, postOptionsListener)
        }
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        holder.bind(getItem(position), userPowerLevel)
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
                        holder.bind(getItem(position), userPowerLevel)
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