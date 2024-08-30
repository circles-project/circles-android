package org.futo.circles.view

import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.R
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.invisible
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.databinding.ViewVideoPlaybackBinding
import org.futo.circles.feature.timeline.list.OnVideoPlayBackStateListener
import org.futo.circles.feature.timeline.list.holder.VideoPlaybackViewHolder

class VideoPlaybackView(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(context, attrs) {

    private val binding = ViewVideoPlaybackBinding.inflate(LayoutInflater.from(context), this)

    private var videoPlayer: ExoPlayer? = null
    private var videoPlaybackListener: OnVideoPlayBackStateListener? = null


    fun setup(
        videoPlayer: ExoPlayer,
        videoPlaybackListener: OnVideoPlayBackStateListener,
        mediaCoverClick: () -> Unit,
        mediaCoverLongClick: () -> Unit,
        fullScreenButtonClick: () -> Unit,
        videoViewClick: () -> Unit
    ) {
        this.videoPlayer = videoPlayer
        this.videoPlaybackListener = videoPlaybackListener
        binding.ivMediaContent.setOnClickListener { mediaCoverClick.invoke() }
        binding.ivMediaContent.setOnLongClickListener { mediaCoverLongClick.invoke(); true }
        binding.ivFullScreen.setOnClickListener { fullScreenButtonClick.invoke() }
        binding.videoView.setOnClickListener { videoViewClick.invoke() }
    }

    fun getMediaCoverView() = binding.ivMediaContent


    fun bindVideoView(content: MediaContent) {
        with(binding.videoView) {
            player = null
            post {
                val size = content.calculateThumbnailSize(width)
                updateLayoutParams {
                    width = size.width
                    height = size.height
                }
            }
        }
        binding.tvDuration.text = content.mediaFileData.duration
    }

    fun playVideo(uri: Uri?, holder: VideoPlaybackViewHolder) {
        uri?.let {
            with(binding) {
                tvDuration.gone()
                videoView.visible()
                ivVideoIndicator.gone()
                ivMediaContent.gone()
            }
            videoPlaybackListener?.onVideoPlaybackStateChanged(holder, true)
            binding.videoView.player = videoPlayer
            videoPlayer?.setMediaItem(MediaItem.fromUri(it))
            videoPlayer?.prepare()
            videoPlayer?.play()
        } ?: run {
            binding.vLoadingView.apply {
                visible()
                setProgress(ResLoadingData(R.string.downloading))
            }
        }
    }

    fun stopVideo(holder: VideoPlaybackViewHolder, shouldNotify: Boolean = true) {
        if (shouldNotify) videoPlaybackListener?.onVideoPlaybackStateChanged(holder, false)
        videoPlayer?.stop()
        with(binding) {
            videoView.player = null
            tvDuration.visible()
            ivVideoIndicator.visible()
            ivMediaContent.visible()
            videoView.invisible()
        }
    }
}