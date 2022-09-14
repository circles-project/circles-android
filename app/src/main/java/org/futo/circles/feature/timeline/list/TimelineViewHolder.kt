package org.futo.circles.feature.timeline.list

import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.databinding.ImagePostViewBinding
import org.futo.circles.databinding.ViewPollPostBinding
import org.futo.circles.databinding.ViewTextPostBinding
import org.futo.circles.databinding.ViewVideoPostBinding
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
) : PostViewHolder(inflate(parent, ViewTextPostBinding::inflate), userPowerLevel) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewTextPostBinding
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
    override val uploadMediaTracker = UploadMediaTracker()

    init {
        binding.lImagePost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        val content = post.content as? ImageContent ?: return
        val image = binding.imageItem.ivGalleryImage
        image.post {
            val size = content.calculateSize(image.width)
            image.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        binding.vLoadingImage.gone()
        uploadMediaTracker.track(post.id, binding.vLoadingImage)
        content.mediaContentData.loadEncryptedIntoWithAspect(image, content.aspectRatio)
    }
}

class VideoPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    userPowerLevel: Int
) : PostViewHolder(inflate(parent, ViewVideoPostBinding::inflate), userPowerLevel),
    UploadMediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewVideoPostBinding
    override val postLayout: PostLayout = binding.lVideoPost
    override val uploadMediaTracker = UploadMediaTracker()

    init {
        binding.lVideoPost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        val content = post.content as? VideoContent ?: return
        val image = binding.videoItem.ivVideoCover
        image.post {
            val size = content.calculateSize(image.width)
            image.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        binding.vLoadingView.gone()
        uploadMediaTracker.track(post.id, binding.vLoadingView)
        content.mediaContentData.loadEncryptedIntoWithAspect(image, content.aspectRatio)
        binding.videoItem.tvDuration.text = content.duration
    }
}

class PollPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    userPowerLevel: Int
) : PostViewHolder(inflate(parent, ViewPollPostBinding::inflate), userPowerLevel) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewPollPostBinding
    override val postLayout: PostLayout = binding.lPollPost

    init {
        binding.lPollPost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        (post.content as? PollContent)?.let {
            binding.pollContentView.setup(it)
        }
    }
}