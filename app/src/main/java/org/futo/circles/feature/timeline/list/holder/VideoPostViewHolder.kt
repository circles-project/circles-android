package org.futo.circles.feature.timeline.list.holder

import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import org.futo.circles.core.base.list.ViewBindingHolder
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
import org.matrix.android.sdk.api.session.file.ContentDownloadStateTracker

class VideoPostViewHolder(
    parent: ViewGroup,
    postOptionsListener: PostOptionsListener,
    isThread: Boolean,
    private val downloadMediaTracker: ContentDownloadStateTracker,
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
    private val downloadListener: ContentDownloadStateTracker.UpdateListener =
        MediaProgressHelper.getDownloadListener(binding.vLoadingView)

    init {
        setListeners()
        binding.videoView.apply {
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
            //bindMediaCover(content, ivMediaContent)
            videoView.post {
                val size = content.calculateThumbnailSize(videoView.width)
                videoView.updateLayoutParams {
                    width = size.width
                    height = size.height
                }
            }
            videoPlayer.setMediaItem(MediaItem.fromUri(content.mediaFileData.fileUrl))
            videoPlayer.prepare()
            tvDuration.text = content.mediaFileData.duration
            uploadMediaTracker.track(post.id, uploadListener)
            downloadMediaTracker.track(post.id, downloadListener)
        }
    }

    override fun unTrackMediaLoading() {
        val key = post?.id ?: return
        uploadMediaTracker.untrack(key, uploadListener)
        downloadMediaTracker.unTrack(key, downloadListener)
    }
}