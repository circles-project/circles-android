package com.futo.circles.feature.groups.timeline.list

import android.util.Size
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.futo.circles.base.ViewBindingHolder
import com.futo.circles.databinding.ImagePostViewBinding
import com.futo.circles.databinding.TextPostViewBinding
import com.futo.circles.extensions.loadEncryptedImage
import com.futo.circles.model.ImageContent
import com.futo.circles.model.Post
import com.futo.circles.model.PostItemPayload
import com.futo.circles.model.TextContent
import com.futo.circles.feature.view.GroupPostListener
import com.futo.circles.feature.view.PostLayout

sealed class GroupPostViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {

    abstract val postLayout: PostLayout

    open fun bind(post: Post) {
        postLayout.setData(post)
    }

    fun bindPayload(payload: PostItemPayload) {
        postLayout.setPayload(payload)
    }
}

class TextPostViewHolder(
    parent: ViewGroup,
    postListener: GroupPostListener
) : GroupPostViewHolder(inflate(parent, TextPostViewBinding::inflate)) {

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
    postListener: GroupPostListener
) : GroupPostViewHolder(inflate(parent, ImagePostViewBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ImagePostViewBinding
    override val postLayout: PostLayout = binding.lImagePost

    init {
        binding.lImagePost.setListener(postListener)
    }

    override fun bind(post: Post) {
        super.bind(post)

        (post.content as? ImageContent)?.let {
            val imageWith = binding.ivContent.width
            val size = Size(imageWith, (imageWith / it.aspectRatio).toInt())
            binding.ivContent.loadEncryptedImage(it, size)
        }
    }
}