package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ViewVideoPostBinding
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.feature.timeline.list.UploadMediaTracker
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.PostStatusView
import org.futo.circles.view.ReadMoreTextView

class VideoPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewVideoPostBinding::inflate), postOptionsListener, isThread),
    MediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewVideoPostBinding
    override val uploadMediaTracker = UploadMediaTracker()
    override val postLayout: ViewGroup
        get() = binding.lCard
    override val postHeader: PostHeaderView
        get() = binding.postHeader
    override val postFooter: PostFooterView
        get() = binding.postFooter
    override val postStatus: PostStatusView
        get() = binding.vPostStatus
    override val readMoreTextView: ReadMoreTextView
        get() = binding.tvTextContent

    init {
        setListeners()
        binding.ivMediaContent.apply {
            setOnClickListener {
                post?.let { optionsListener.onShowPreview(it.postInfo.roomId, it.id) }
            }
            setOnLongClickListener {
                postHeader.showMenu()
                true
            }
        }
    }

    override fun bind(post: Post) {
        super.bind(post)
        with(binding) {
            val content = (post.content as? MediaContent) ?: return
            bindMediaCaption(content, tvTextContent)
            bindMediaCover(content, ivMediaContent)
            tvDuration.text = content.mediaFileData.duration
            uploadMediaTracker.track(post.id, vLoadingView)
        }
    }
}