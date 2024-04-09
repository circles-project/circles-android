package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ViewImagePostBinding
import org.futo.circles.feature.timeline.list.MediaProgressHelper
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.PostStatusView
import org.futo.circles.view.ReadMoreTextView
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

class ImagePostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    private val uploadMediaTracker: ContentUploadStateTracker,
    isThread: Boolean
) : PostViewHolder(inflate(parent, ViewImagePostBinding::inflate), postOptionsListener, isThread),
    MediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewImagePostBinding
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

    private val uploadListener: ContentUploadStateTracker.UpdateListener =
        MediaProgressHelper.getUploadListener(binding.vLoadingView)

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


    override fun bindHolderSpecific(post: Post) {
        with(binding) {
            vLoadingView.gone()
            val content = (post.content as? MediaContent) ?: return
            bindMediaCaption(content, tvTextContent)
            bindMediaCover(content, ivMediaContent)
            uploadMediaTracker.track(post.id, uploadListener)
        }
    }

    override fun unTrackMediaLoading() {
        val key = post?.id ?: return
        uploadMediaTracker.untrack(key, uploadListener)
    }

}