package org.futo.circles.view


import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.ImageUtils
import org.futo.circles.core.utils.VideoUtils
import org.futo.circles.core.utils.VideoUtils.getVideoDuration
import org.futo.circles.core.utils.VideoUtils.getVideoDurationString
import org.futo.circles.databinding.ViewPreviewPostBinding
import org.futo.circles.extensions.convertDpToPixel
import org.futo.circles.extensions.loadImage
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
import org.futo.circles.provider.MatrixSessionProvider
import org.futo.circles.view.markdown.TextStyle
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
        setOnClickListener { requestFocusOnText() }
        binding.etTextPost.doAfterTextChanged {
            listener?.onPostContentAvailable(it?.toString()?.isNotBlank() == true)
        }
        binding.ivRemoveImage.setOnClickListener {
            setTextContent()
        }
        updateContentView()
    }

    fun setListener(
        previewPostListener: PreviewPostListener,
        onHighlightTextStyle: (TextStyle?) -> Unit
    ) {
        listener = previewPostListener
        binding.etTextPost.setHighlightSelectedSpanListener(onHighlightTextStyle)
    }

    fun setText(message: String) {
        binding.etTextPost.setText(message)
        setTextContent()
    }

    fun setTextStyle(style: TextStyle, isStop: Boolean) {
        binding.etTextPost.triggerStyle(style, isStop)
    }

    fun insertEmoji(unicode: String) {
        binding.etTextPost.insertEmoji(unicode)
    }

    fun insertMention() {
        binding.etTextPost.insertMention()
    }

    fun insertLink(title: String?, link: String) {
        binding.etTextPost.addLinkSpan(title, link)
    }

    fun setMedia(contentUri: Uri, mediaType: MediaType) {
        val caption = binding.etTextPost.text.toString().trim()
        postContent = MediaPostContent(caption, contentUri, mediaType)
        updateContentView()
        loadMediaCover(contentUri, mediaType)
        val isVideo = mediaType == MediaType.Video
        binding.lMediaContent.videoGroup.setIsVisible(isVideo)
        if (isVideo)
            binding.lMediaContent.tvDuration.text =
                getVideoDurationString(getVideoDuration(context, contentUri))

        listener?.onPostContentAvailable(true)
    }

    fun getPostContent() = (postContent as? MediaPostContent)?.copy(
        caption = binding.etTextPost.text.toString().trim().takeIf { it.isNotEmpty() }
    ) ?: TextPostContent(binding.etTextPost.text.toString().trim())

    private fun updateContentView() {
        val isTextContent = postContent is TextPostContent || postContent == null
        binding.mediaContentGroup.setIsVisible(!isTextContent)
        if (isTextContent) requestFocusOnText()
        binding.etTextPost.setPadding(
            context.convertDpToPixel(12f).toInt(),
            0, context.convertDpToPixel(12f).toInt(),
            if (isTextContent) context.convertDpToPixel(64f).toInt() else 0
        )
    }

    private fun setTextContent() {
        postContent = null
        updateContentView()
        listener?.onPostContentAvailable(binding.etTextPost.text.toString().isNotBlank())
    }

    private fun loadMediaCover(uri: Uri, mediaType: MediaType) {
        val size = when (mediaType) {
            MediaType.Image -> ImageUtils.getImageResolution(context, uri)
            MediaType.Video -> VideoUtils.getVideoResolution(context, uri)
        }
        val aspectRatio = size.width.toFloat() / size.height.toFloat()
        binding.lMediaContent.ivCover.post {
            binding.lMediaContent.ivCover.updateLayoutParams {
                width = binding.lvContent.width
                height = (width / aspectRatio).toInt()
            }
        }
        binding.lMediaContent.ivCover.loadImage(uri.toString())
    }

    private fun requestFocusOnText() {
        binding.etTextPost.post {
            requestFocus()
            binding.etTextPost.setSelection(binding.etTextPost.text.length)
            showKeyboard()
        }
    }

    private fun showKeyboard() {
        val inputMethodManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.showSoftInput(binding.etTextPost, 0)
    }

    private fun getMyUser(): User? {
        val session = MatrixSessionProvider.currentSession
        return session?.myUserId?.let { session.getUser(it) }
    }
}