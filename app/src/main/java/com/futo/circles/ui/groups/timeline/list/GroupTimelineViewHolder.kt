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
import com.futo.circles.model.PostItemPayload
import com.futo.circles.model.TextContent
import com.futo.circles.ui.view.GroupPostListener
import com.futo.circles.ui.view.PostLayout
import org.matrix.android.sdk.api.session.content.ContentUrlResolver

sealed class GroupPostViewHolder(view: View, private val urlResolver: ContentUrlResolver?) :
    RecyclerView.ViewHolder(view) {

    abstract val postLayout: PostLayout

    open fun bind(post: Post) {
        postLayout.setData(post, urlResolver)
    }

    fun bindPayload(payload: PostItemPayload) {
        postLayout.setPayload(payload)
    }
}

class TextPostViewHolder(
    parent: ViewGroup,
    postListener: GroupPostListener,
    private val urlResolver: ContentUrlResolver?
) : GroupPostViewHolder(inflate(parent, TextPostViewBinding::inflate), urlResolver) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as TextPostViewBinding
    override val postLayout: PostLayout = binding.lTextPost

    init {
        binding.lTextPost.setListener(postListener)
    }

    override fun bind(post: Post) {
        super.bind(post)

        (post.content as? TextContent)?.let {
            binding.tvContent.text = it.message
        }
    }
}

class ImagePostViewHolder(
    parent: ViewGroup,
    postListener: GroupPostListener,
    private val urlResolver: ContentUrlResolver?
) : GroupPostViewHolder(inflate(parent, ImagePostViewBinding::inflate), urlResolver) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ImagePostViewBinding
    override val postLayout: PostLayout = binding.lImagePost

    init {
        binding.lImagePost.setListener(postListener)
    }

    override fun bind(post: Post) {
        super.bind(post)

        (post.content as? ImageContent)?.let {
            binding.ivContent.loadMatrixThumbnail(it.url, urlResolver)
        }
    }
}