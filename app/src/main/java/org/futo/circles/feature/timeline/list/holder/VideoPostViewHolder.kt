package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ViewVideoPostBinding
import org.futo.circles.feature.timeline.list.OnVideoPlayBackStateListener
import org.futo.circles.feature.timeline.list.PostOptionsListener
import org.futo.circles.view.PostFooterView
import org.futo.circles.view.PostHeaderView
import org.futo.circles.view.ReadMoreTextView

class VideoPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean,
    videoPlayer: ExoPlayer,
    videoPlaybackListener: OnVideoPlayBackStateListener
) : PostViewHolder(inflate(parent, ViewVideoPostBinding::inflate), postOptionsListener, isThread),
    VideoPlaybackViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ViewVideoPostBinding
    override val postLayout: ViewGroup
        get() = binding.lCard
    override val postHeader: PostHeaderView
        get() = binding.postHeader
    override val postFooter: PostFooterView
        get() = binding.postFooter
    override val readMoreTextView: ReadMoreTextView
        get() = binding.tvTextContent


    init {
        setListeners()
        binding.videoPlaybackView.setup(
            videoPlayer,
            videoPlaybackListener,
            mediaCoverClick = {
                playVideo()
            },
            mediaCoverLongClick = {
                postHeader.showMenu()
            },
            fullScreenButtonClick = {
                post?.let { optionsListener.onShowPreview(it.postInfo.roomId, it.id) }
            },
            videoViewClick = {
                stopVideo()
            }
        )
    }

    override fun bindHolderSpecific(post: Post) {
        with(binding) {
            val content = (post.content as? MediaContent) ?: return
            bindMediaCaption(content, tvTextContent)
            bindMediaCover(content, videoPlaybackView.getMediaCoverView())
            videoPlaybackView.bindVideoView(content)
        }
    }

    override fun stopVideo(shouldNotify: Boolean) {
        binding.videoPlaybackView.stopVideo(this, shouldNotify)
    }

    override fun playVideo() {
        val uri = (post?.content as? MediaContent)?.mediaFileData?.videoUri
        binding.videoPlaybackView.playVideo(uri, this)
    }
}