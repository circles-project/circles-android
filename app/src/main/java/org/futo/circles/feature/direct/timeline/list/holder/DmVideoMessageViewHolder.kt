package org.futo.circles.feature.direct.timeline.list.holder

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.media3.exoplayer.ExoPlayer
import com.google.android.material.imageview.ShapeableImageView
import org.futo.circles.core.base.list.ViewBindingHolder
import org.futo.circles.core.model.DmTimelineMessage
import org.futo.circles.core.model.MediaContent
import org.futo.circles.databinding.ListItemVideoDmBinding
import org.futo.circles.feature.direct.timeline.listeners.DmOptionsListener
import org.futo.circles.feature.timeline.list.OnVideoPlayBackStateListener
import org.futo.circles.feature.timeline.list.holder.VideoPlaybackViewHolder
import org.futo.circles.view.DmFooterView
import org.futo.circles.view.VideoPlaybackView


class DmVideoMessageViewHolder(
    parent: ViewGroup,
    dmOptionsListener: DmOptionsListener,
    private val videoPlayer: ExoPlayer,
    private val videoPlaybackListener: OnVideoPlayBackStateListener
) : DmViewHolder(inflate(parent, ListItemVideoDmBinding::inflate), dmOptionsListener),
    VideoPlaybackViewHolder {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemVideoDmBinding

    override val rootMessageLayout: FrameLayout
        get() = binding.rootMessageLayout
    override val dmBackground: ShapeableImageView
        get() = binding.ivBackground
    override val dmFooter: DmFooterView
        get() = binding.dmFooter

    private val videoPlaybackView: VideoPlaybackView
        get() = binding.videoPlaybackLayout

    init {
        initListeners()
    }

    override fun bindHolderSpecific(dmMessage: DmTimelineMessage) {
        bindVideo(dmMessage)
    }

    override fun stopVideo(shouldNotify: Boolean) {
        videoPlaybackView.stopVideo(this, shouldNotify)
    }

    override fun playVideo() {
        val uri = (dmMessage?.content as? MediaContent)?.mediaFileData?.videoUri
        videoPlaybackView.playVideo(uri, this)
    }

    private fun initListeners() {
        setListeners()
        videoPlaybackView.setup(
            videoPlayer,
            videoPlaybackListener,
            mediaCoverClick = {
                playVideo()
            },
            mediaCoverLongClick = {
                dmMessage?.let { dmOptionsListener.onShowMenuClicked(it.id) }
            },
            fullScreenButtonClick = {
                dmMessage?.let { dmOptionsListener.onShowPreview(it.id) }
            },
            videoViewClick = {
                stopVideo()
            }
        )
    }

    private fun bindVideo(dmMessage: DmTimelineMessage) {
        val content = dmMessage.content as? MediaContent ?: return
        bindMediaCover(content, videoPlaybackView.getMediaCoverView())
        videoPlaybackView.bindVideoView(content)
    }

}