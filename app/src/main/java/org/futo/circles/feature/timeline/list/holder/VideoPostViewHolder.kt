package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.invisible
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ViewVideoPostBinding
import org.futo.circles.feature.timeline.list.MediaProgressHelper
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.PostStatusView
import org.futo.circles.view.ReadMoreTextView
import org.matrix.android.sdk.api.session.content.ContentUploadStateTracker

class VideoPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean,
    private val uploadMediaTracker: ContentUploadStateTracker,
    private val videoPlayer: ExoPlayer
) : PostViewHolder(inflate(parent, ViewVideoPostBinding::inflate), postOptionsListener, isThread),
    MediaViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewVideoPostBinding
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
        binding.ivFullScreen.setOnClickListener {
            post?.let { optionsListener.onShowPreview(it.postInfo.roomId, it.id) }
        }
        binding.ivMediaContent.apply {
            setOnClickListener { playVideo() }
            setOnLongClickListener {
                postHeader.showMenu()
                true
            }
        }
        binding.videoView.setOnClickListener { stopVideo() }
    }

    override fun bind(post: Post) {
        super.bind(post)
        with(binding) {
            val content = (post.content as? MediaContent) ?: return
            bindMediaCaption(content, tvTextContent)
            bindMediaCover(content, ivMediaContent)
            bindVideoView(content)
            tvDuration.text = content.mediaFileData.duration
            uploadMediaTracker.track(post.id, uploadListener)
        }
    }
    
    private fun bindVideoView(content: MediaContent) {
        with(binding) {
            videoView.player = null
            videoView.post {
                val size = content.calculateThumbnailSize(videoView.width)
                videoView.updateLayoutParams {
                    width = size.width
                    height = size.height
                }
            }
        }
    }

    override fun unTrackMediaLoading() {
        post?.id?.let { uploadMediaTracker.untrack(it, uploadListener) }
    }

    private fun playVideo() {
        with(binding) {
            tvDuration.gone()
            videoView.visible()
            ivVideoIndicator.gone()
            ivMediaContent.gone()
        }
        val uri = (post?.content as? MediaContent)?.mediaFileData?.videoUri
        uri?.let {
            binding.videoView.player = videoPlayer
            videoPlayer.setMediaItem(MediaItem.fromUri(it))
            videoPlayer.prepare()
            videoPlayer.play()
        }
    }

    private fun stopVideo() {
        videoPlayer.stop()
        binding.tvDuration.visible()
        binding.ivVideoIndicator.visible()
        binding.ivMediaContent.visible()
        binding.videoView.invisible()
    }
}