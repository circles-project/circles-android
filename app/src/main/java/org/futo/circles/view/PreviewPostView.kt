package org.futo.circles.view


import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.VideoUtils.getVideoDuration
import org.futo.circles.core.utils.VideoUtils.getVideoDurationString
import org.futo.circles.databinding.ViewPreviewPostBinding
import org.futo.circles.extensions.loadImage
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUser
import org.matrix.android.sdk.api.session.user.model.User

interface PreviewPostListener {
    fun onPostContentAvailable(isAvailable: Boolean)
}

class PreviewPostView(
    context: Context,
    attrs: AttributeSet? = null,
) : ConstraintLayout(context, attrs) {

    private val binding =
        ViewPreviewPostBinding.inflate(LayoutInflater.from(context), this)

    private var listener: PreviewPostListener? = null

    private var postContent: CreatePostContent? = null

    init {
        getMyUser()?.let {
            binding.postHeader.bindViewData(
                it.userId,
                it.notEmptyDisplayName(),
                it.avatarUrl,
                System.currentTimeMillis(),
                true
            )
        }
        binding.etTextPost.doAfterTextChanged {
            listener?.onPostContentAvailable(it?.toString()?.isNotBlank() == true)
        }
        binding.lImageContent.ivRemoveImage.setOnClickListener {
            setTextContent()
        }
        binding.lVideoContent.ivRemoveVideo.setOnClickListener {
            setTextContent()
        }
        updateContentView()
    }

    fun setListener(previewPostListener: PreviewPostListener) {
        listener = previewPostListener
    }

    fun setMedia(contentUri: Uri, mediaType: MediaType) {
        postContent = MediaPostContent(contentUri, mediaType)
        updateContentView()
        when (mediaType) {
            MediaType.Image -> {
                binding.lImageContent.ivImageContent.loadImage(contentUri.toString())
            }
            MediaType.Video -> {
                binding.lVideoContent.apply {
                    ivVideoCover.loadImage(contentUri.toString())
                    tvDuration.text = getVideoDurationString(getVideoDuration(context, contentUri))
                }
            }
        }
        listener?.onPostContentAvailable(true)
    }

    fun getPostContent() = postContent ?: TextPostContent(binding.etTextPost.text.toString().trim())

    private fun updateContentView() {
        binding.lVideoContent.root.setIsVisible((postContent as? MediaPostContent)?.mediaType == MediaType.Video)
        binding.lImageContent.root.setIsVisible((postContent as? MediaPostContent)?.mediaType == MediaType.Image)
        binding.etTextPost.setIsVisible(postContent is TextPostContent || postContent == null)
    }

    private fun setTextContent() {
        postContent = null
        updateContentView()
        listener?.onPostContentAvailable(binding.etTextPost.text?.toString()?.isNotBlank() == true)
    }

    private fun getMyUser(): User? {
        val session = MatrixSessionProvider.currentSession
        return session?.myUserId?.let { session.getUser(it) }
    }
}