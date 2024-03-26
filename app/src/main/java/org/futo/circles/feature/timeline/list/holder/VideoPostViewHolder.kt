package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.updateLayoutParams
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.loadEncryptedThumbOrFullIntoWithAspect
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ViewVideoPostBinding
import org.futo.circles.feature.timeline.list.UploadMediaTracker
import org.futo.circles.feature.timeline.list.UploadMediaViewHolder
import org.futo.circles.view.PostLayout
import org.futo.circles.view.PostOptionsListener

class VideoPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewVideoPostBinding::inflate), isThread),
    UploadMediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewVideoPostBinding
    override val postLayout: PostLayout = binding.lPost
    override val uploadMediaTracker = UploadMediaTracker()

    init {
        binding.lPost.setListener(postOptionsListener)
        handleLinkClick(binding.tvTextContent)
    }

    override fun bind(post: Post) {
        super.bind(post)
        val content = (post.content as? MediaContent) ?: return
        bindMediaContent(content)
        uploadMediaTracker.track(post.id, binding.vLoadingView)
    }


    private fun bindMediaContent(content: MediaContent) {
        bindMediaCaption(content)
        bindMediaCover(content)
        binding.tvDuration.text = content.mediaFileData.duration
    }

    private fun bindMediaCaption(content: MediaContent) {
        binding.tvTextContent.apply {
            val caption = content.captionSpanned
            setIsVisible(caption != null)
            caption?.let { setText(it, TextView.BufferType.SPANNABLE) }
        }
    }

    private fun bindMediaCover(content: MediaContent) {
        val image = binding.ivMediaContent
        image.post {
            val size = content.calculateThumbnailSize(image.width)
            image.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        content.loadEncryptedThumbOrFullIntoWithAspect(image)
    }
}