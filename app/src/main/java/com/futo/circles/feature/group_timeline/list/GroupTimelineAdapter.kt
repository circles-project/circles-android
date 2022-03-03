package com.futo.circles.feature.group_timeline.list

import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.model.Post
import com.futo.circles.model.PostContentType
import com.futo.circles.model.PostItemPayload
import com.futo.circles.model.RootPost
import com.futo.circles.view.GroupPostListener

class GroupTimelineAdapter(
    private val postListener: GroupPostListener,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<Post, GroupPostViewHolder>(PayloadIdEntityCallback { _, new ->
    (new as? RootPost)?.let { rootPost ->
        PostItemPayload(
            repliesCount = rootPost.getRepliesCount(),
            isRepliesVisible = rootPost.isRepliesVisible,
            hasReplies = rootPost.hasReplies()
        )
    }
}) {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).content.type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupPostViewHolder {
        return when (PostContentType.values()[viewType]) {
            PostContentType.TEXT_CONTENT -> TextPostViewHolder(parent, postListener)
            PostContentType.IMAGE_CONTENT -> ImagePostViewHolder(parent, postListener)
        }
    }

    override fun onBindViewHolder(holder: GroupPostViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    override fun onBindViewHolder(
        holder: GroupPostViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
        if (payloads.isNullOrEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            payloads.forEach {
                (it as? PostItemPayload)?.let { payload -> holder.bindPayload(payload) }
            }
        }
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

}