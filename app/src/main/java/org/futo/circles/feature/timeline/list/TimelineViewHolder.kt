package org.futo.circles.feature.timeline.list

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.core.view.updateLayoutParams
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.futo.circles.R
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadEncryptedThumbOrFullIntoWithAspect
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.PollContent
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.TextContent
import org.futo.circles.databinding.ViewPollPostBinding
import org.futo.circles.databinding.ViewTextMediaPostBinding
import org.futo.circles.feature.timeline.InternalLinkMovementMethod
import org.futo.circles.model.PostItemPayload
import org.futo.circles.view.PostLayout
import org.futo.circles.view.PostOptionsListener
import org.matrix.android.sdk.api.extensions.tryOrNull


sealed class PostViewHolder(view: View, private val isThread: Boolean) :
    RecyclerView.ViewHolder(view) {

    abstract val postLayout: PostLayout

    @CallSuper
    open fun bind(post: Post) {
        postLayout.setData(post, isThread)
    }

    fun bindPayload(payload: PostItemPayload) {
        postLayout.setPayload(payload)
    }
}

class TextMediaPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewTextMediaPostBinding::inflate), isThread),
    UploadMediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewTextMediaPostBinding
    override val postLayout: PostLayout = binding.lPost
    override val uploadMediaTracker = UploadMediaTracker()

    init {
        binding.lPost.setListener(postOptionsListener)
        handleTextClick()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun handleTextClick() {
        binding.tvTextContent.apply {
            movementMethod = InternalLinkMovementMethod(object : OnLinkClickedListener {
                override fun onLinkClicked(url: String) {
                    showLinkConfirmation(context, url)
                }
            })
            setOnTouchListener { v, event ->
                if (event.action == MotionEvent.ACTION_DOWN) v.requestFocus()
                false
            }
        }
    }

    private fun showLinkConfirmation(context: Context, url: String) {
        MaterialAlertDialogBuilder(context)
            .setTitle(R.string.do_you_want_to_open_this_url)
            .setMessage(url)
            .setPositiveButton(android.R.string.ok) { dialogInterface, _ ->
                tryOrNull {
                    context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton(android.R.string.cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .show()
    }

    override fun bind(post: Post) {
        super.bind(post)
        binding.vLoadingView.gone()
        when (val content = post.content) {
            is TextContent -> bindTextPost(content)
            is MediaContent -> {
                bindMediaContent(content)
                uploadMediaTracker.track(post.id, binding.vLoadingView)
            }

            else -> return
        }
    }

    private fun bindTextPost(content: TextContent) {
        binding.tvTextContent.apply {
            setText(content.messageSpanned, TextView.BufferType.SPANNABLE)
            visible()
        }
        binding.vMediaContent.lMedia.gone()
    }

    private fun bindMediaContent(content: MediaContent) {
        bindMediaCaption(content)
        bindMediaCover(content)
        binding.vMediaContent.videoGroup.setIsVisible(content.getMediaType() == MediaType.Video)
        binding.vMediaContent.tvDuration.text = content.mediaFileData.duration
    }

    private fun bindMediaCaption(content: MediaContent) {
        binding.tvTextContent.apply {
            val caption = content.captionSpanned
            setIsVisible(caption != null)
            caption?.let { setText(it, TextView.BufferType.SPANNABLE) }
        }
    }

    private fun bindMediaCover(content: MediaContent) {
        val image = binding.vMediaContent.ivCover
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

class PollPostViewHolder(
    parent: ViewGroup,
    private val postOptionsListener: PostOptionsListener,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewPollPostBinding::inflate), isThread) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewPollPostBinding
    override val postLayout: PostLayout = binding.lPollPost

    init {
        binding.lPollPost.setListener(postOptionsListener)
    }

    override fun bind(post: Post) {
        super.bind(post)
        (post.content as? PollContent)?.let {
            binding.pollContentView.setup(it) { optionId ->
                postOptionsListener.onPollOptionSelected(post.postInfo.roomId, post.id, optionId)
            }
        }
    }
}