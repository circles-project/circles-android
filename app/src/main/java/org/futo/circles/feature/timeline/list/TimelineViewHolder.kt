package org.futo.circles.feature.timeline.list

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ImagePostViewBinding
import org.futo.circles.databinding.TextPostViewBinding
import org.futo.circles.databinding.VideoPostViewBinding
import org.futo.circles.extensions.gone
import org.futo.circles.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.model.*
import org.futo.circles.view.PostLayout
import org.futo.circles.view.PostOptionsListener

sealed class PostViewHolder(view: View, private val userPowerLevel: Int) :
    RecyclerView.ViewHolder(view) {

    abstract val postLayout: PostLayout

    open fun bind(post: Post) {
        postLayout.setData(post, userPowerLevel)
    }

    fun bindPayload(payload: PostItemPayload) {
        postLayout.setPayload(payload)
    }
}

class TextPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    userPowerLevel: Int
) : PostViewHolder(inflate(parent, TextPostViewBinding::inflate), userPowerLevel) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as TextPostViewBinding
    override val postLayout: PostLayout = binding.lTextPost

    init {
        binding.lTextPost.setListener(postOptionsListener)
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
    postOptionsListener: PostOptionsListener,
    userPowerLevel: Int
) : PostViewHolder(inflate(parent, ImagePostViewBinding::inflate), userPowerLevel),
    UploadMediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ImagePostViewBinding
    override val postLayout: PostLayout = binding.lImagePost

    init {
        binding.lImagePost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        binding.vLoadingImage.gone()
        track(post.id, binding.vLoadingImage)
        (post.content as? ImageContent)?.let {
            it.mediaContentData.loadEncryptedIntoWithAspect(binding.ivContent, it.aspectRatio)
        }
    }
}

class VideoPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    userPowerLevel: Int
) : PostViewHolder(inflate(parent, VideoPostViewBinding::inflate), userPowerLevel),
    UploadMediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as VideoPostViewBinding
    override val postLayout: PostLayout = binding.lVideoPost

    init {
        binding.lVideoPost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        binding.vLoadingView.gone()
        track(post.id, binding.vLoadingView)
        (post.content as? VideoContent)?.let {
            it.mediaContentData.loadEncryptedIntoWithAspect(
                binding.videoItem.ivVideoCover, it.aspectRatio
            )
            binding.videoItem.tvDuration.text = it.duration
        }
    }
}