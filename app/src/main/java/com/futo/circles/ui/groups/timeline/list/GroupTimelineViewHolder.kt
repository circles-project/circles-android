package com.futo.circles.ui.groups.timeline.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.ImagePostViewBinding
import com.futo.circles.databinding.TextPostViewBinding
import com.futo.circles.extensions.loadMatrixThumbnail
import com.futo.circles.model.ImageContent
import com.futo.circles.model.Post
import com.futo.circles.model.TextContent
import com.futo.circles.ui.view.GroupPostListener
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

sealed class GroupPostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(post: Post)
}

class TextPostViewHolder(
    parent: ViewGroup,
    postListener: GroupPostListener,
    private val urlResolver: ContentUrlResolver?
) : GroupPostViewHolder(inflate(parent, TextPostViewBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as TextPostViewBinding

    init {
        binding.lTextPost.setListener(postListener)
    }

    override fun bind(post: Post) {
        binding.lTextPost.setData(post, urlResolver)

        (post.content as? TextContent)?.let {
            binding.tvContent.text = it.message
        }
    }
}

class ImagePostViewHolder(
    parent: ViewGroup,
    postListener: GroupPostListener,
    private val urlResolver: ContentUrlResolver?
) : GroupPostViewHolder(inflate(parent, ImagePostViewBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ImagePostViewBinding

    init {
        binding.lImagePost.setListener(postListener)
    }

    override fun bind(post: Post) {
        binding.lImagePost.setData(post, urlResolver)

        (post.content as? ImageContent)?.let {
            binding.ivContent.loadMatrixThumbnail(it.url, urlResolver)
        }
    }
}