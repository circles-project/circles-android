package org.futo.circles.view


import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import org.futo.circles.core.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.core.extensions.loadImage
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.ImageUtils
import org.futo.circles.core.utils.VideoUtils
import org.futo.circles.core.utils.VideoUtils.getVideoDuration
import org.futo.circles.core.utils.VideoUtils.getVideoDurationString
import org.futo.circles.databinding.ViewPreviewPostBinding
import org.futo.circles.extensions.convertDpToPixel
import org.futo.circles.feature.timeline.post.markdown.MarkdownParser
import org.futo.circles.feature.timeline.post.markdown.span.TextStyle
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
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
    private var canEditMedia: Boolean = true

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

    fun setup(
        previewPostListener: PreviewPostListener,
        onHighlightTextStyle: (List<TextStyle>) -> Unit,
        roomId: String
    ) {
        listener = previewPostListener
        binding.etTextPost.setHighlightSelectedSpanListener(onHighlightTextStyle)
        binding.etTextPost.initMentionsAutocomplete(roomId)
    }

    fun setText(message: String) {
        binding.etTextPost.setText(
            MarkdownParser.markwonBuilder(context).toMarkdown(message),
            TextView.BufferType.SPANNABLE
        )
        setTextContent()
    }

    fun setTextStyle(style: TextStyle, isSelected: Boolean) {
        binding.etTextPost.triggerStyle(style, isSelected)
    }

    fun insertEmoji(unicode: String) {
        binding.etTextPost.insertText(unicode)
    }

    fun insertMention() {
        binding.etTextPost.insertMentionMark()
    }

    fun insertLink(title: String?, link: String) {
        binding.etTextPost.addLinkSpan(title, link)
    }

    fun setMediaFromExistingPost(mediaContent: MediaContent) {
        canEditMedia = false
        val caption = mediaContent.mediaContentInfo.caption ?: ""
        setText(caption)
        val uri = Uri.parse(mediaContent.mediaFileData.fileUrl)
        val mediaType = mediaContent.getMediaType()
        postContent = MediaPostContent(caption, uri, mediaType)
        updateContentView()
        loadMediaCover(mediaContent)
        val isVideo = mediaType == MediaType.Video
        binding.lMediaContent.videoGroup.setIsVisible(isVideo)
        if (isVideo)
            binding.lMediaContent.tvDuration.text = mediaContent.mediaContentInfo.duration

        listener?.onPostContentAvailable(true)
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
        caption = binding.etTextPost.getTextWithMarkdown().trim().takeIf { it.isNotEmpty() }
    ) ?: TextPostContent(binding.etTextPost.getTextWithMarkdown().trim())

    private fun updateContentView() {
        val isTextContent = postContent is TextPostContent || postContent == null
        binding.lMediaContent.lMedia.setIsVisible(!isTextContent)
        binding.ivRemoveImage.setIsVisible(!isTextContent && canEditMedia)
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

    private fun loadMediaCover(mediaContent: MediaContent) {
        val image = binding.lMediaContent.ivCover
        image.post {
            val size = mediaContent.calculateSize(image.width)
            image.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        mediaContent.mediaFileData.loadEncryptedIntoWithAspect(
            image,
            mediaContent.aspectRatio,
            mediaContent.mediaContentInfo.thumbHash
        )
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