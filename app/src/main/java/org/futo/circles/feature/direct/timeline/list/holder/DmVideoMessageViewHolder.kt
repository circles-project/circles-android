package org.futo.circles.feature.direct.timeline.list.holder

import android.view.View
import android.view.ViewGroup
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.Post
import org.futo.circles.databinding.ListItemMyVideoDmBinding
import org.futo.circles.databinding.ListItemOtherVideoDmBinding
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.list.OnVideoPlayBackStateListener
import org.futo.circles.feature.timeline.list.holder.VideoPlaybackViewHolder
import org.futo.circles.view.DmFooterView
import org.futo.circles.view.VideoPlaybackView


abstract class DmVideoMessageViewHolder(
    view: View,
    dmOptionsListener: DmOptionsListener,
    protected open val videoPlayer: ExoPlayer,
    protected open val videoPlaybackListener: OnVideoPlayBackStateListener
) : DmViewHolder(view, dmOptionsListener), VideoPlaybackViewHolder {

    abstract val videoPlaybackView: VideoPlaybackView

    override fun bindHolderSpecific(post: Post) {
        bindVideo(post)
    }

    override fun stopVideo(shouldNotify: Boolean) {
        videoPlaybackView.stopVideo(this, shouldNotify)
    }

    override fun playVideo() {
        val uri = (post?.content as? MediaContent)?.mediaFileData?.videoUri
        videoPlaybackView.playVideo(uri, this)
    }

    protected fun initListeners() {
        setListeners()
        videoPlaybackView.setup(
            videoPlayer,
            videoPlaybackListener,
            mediaCoverClick = {
                playVideo()
            },
            mediaCoverLongClick = {
                post?.let { dmOptionsListener.onShowMenuClicked(it.id) }
            },
            fullScreenButtonClick = {
                post?.let { dmOptionsListener.onShowPreview(it.id) }
            },
            videoViewClick = {
                stopVideo()
            }
        )
    }

    private fun bindVideo(post: Post) {
        val content = post.content as? MediaContent ?: return
        bindMediaCover(content, videoPlaybackView.getMediaCoverView())
        videoPlaybackView.bindVideoView(content)
    }


}


class DmMyVideoMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener,
    override val videoPlayer: ExoPlayer,
    override val videoPlaybackListener: OnVideoPlayBackStateListener
) : DmVideoMessageViewHolder(
    inflate(parent, ListItemMyVideoDmBinding::inflate),
    dmOptionsListener,
    videoPlayer, videoPlaybackListener
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMyVideoDmBinding

    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter
    override val videoPlaybackView: VideoPlaybackView
        get() = binding.videoPlaybackLayout

    init {
        initListeners()
    }

}

class DmOtherVideoMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener,
    override val videoPlayer: ExoPlayer,
    override val videoPlaybackListener: OnVideoPlayBackStateListener
) : DmVideoMessageViewHolder(
    inflate(parent, ListItemOtherVideoDmBinding::inflate),
    dmOptionsListener,
    videoPlayer, videoPlaybackListener
) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemOtherVideoDmBinding

    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter
    override val videoPlaybackView: VideoPlaybackView
        get() = binding.videoPlaybackLayout

    init {
        initListeners()
    }

}