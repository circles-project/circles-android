package com.futo.circles.ui.groups.timeline.list

import android.view.LayoutInflater
import android.view.ViewGroup
import com.futo.circles.base.BaseRvAdapter
import com.futo.circles.databinding.ImagePostViewBinding
import com.futo.circles.databinding.TextPostViewBinding
import com.futo.circles.model.Post
import com.futo.circles.model.PostContentType
import com.futo.circles.ui.view.GroupPostListener
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

class GroupTimelineAdapter(
    private val postListener: GroupPostListener,
    private val urlResolver: ContentUrlResolver?,
    private val onLoadMore: () -> Unit
) : BaseRvAdapter<Post, GroupPostViewHolder>(DefaultIdEntityCallback()) {


    override fun getItemViewType(position: Int): Int {
        return getItem(position).content.type.ordinal
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroupPostViewHolder {
        return when (PostContentType.values()[viewType]) {
            PostContentType.TEXT_CONTENT -> TextPostViewHolder(
                TextPostViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), postListener, urlResolver
            )
            PostContentType.IMAGE_CONTENT -> ImagePostViewHolder(
                ImagePostViewBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false
                ), postListener, urlResolver
            )
        }
    }

    override fun onBindViewHolder(holder: GroupPostViewHolder, position: Int) {
        holder.bind(getItem(position))
        if (position >= itemCount - LOAD_MORE_THRESHOLD) onLoadMore()
    }

    companion object {
        private const val LOAD_MORE_THRESHOLD = 10
    }

}