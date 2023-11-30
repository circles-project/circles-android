package org.futo.circles.feature.timeline.list

import android.annotation.SuppressLint
import android.content.Context
import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContentType
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.PostOptionsListener

class TimelineAdapter(
    context: Context,
    private var userPowerLevel: Int,
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

    private val markwon = MarkdownParser.markwonBuilder(context)

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserPowerLevel(level: Int) {
        userPowerLevel = level
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = getItem(position).content.type.ordinal

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        return when (PostContentType.values()[viewType]) {
            PostContentType.POLL_CONTENT -> PollPostViewHolder(
                parent, postOptionsListener, isThread
            )

            else -> TextMediaPostViewHolder(parent, markwon, postOptionsListener, isThread)
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
                val payload = (it as? PostItemPayload) ?: return@forEach
                if (payload.needToUpdateFullItem) holder.bind(getItem(position), userPowerLevel)
                else holder.bindPayload(payload)
            }
        }
    }

    override fun onViewDetachedFromWindow(holder: PostViewHolder) {
        super.onViewDetachedFromWindow(holder)
        (holder as? UploadMediaViewHolder)?.uploadMediaTracker?.unTrack()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 15
    }

}