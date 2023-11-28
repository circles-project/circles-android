package org.futo.circles.view


import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.core.view.updateLayoutParams
import androidx.core.widget.doAfterTextChanged
import io.element.android.wysiwyg.EditorEditText
import io.element.android.wysiwyg.view.models.InlineFormat
import org.futo.circles.R
import org.futo.circles.core.extensions.loadEncryptedThumbOrFullIntoWithAspect
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
import org.futo.circles.databinding.ViewRichTextMenuButtonBinding
import org.futo.circles.extensions.convertDpToPixel
import org.futo.circles.extensions.showKeyboard
import org.futo.circles.feature.timeline.post.markdown.MarkdownParser
import org.futo.circles.feature.timeline.post.markdown.span.TextStyle
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
import org.matrix.android.sdk.api.session.getUser
import org.matrix.android.sdk.api.session.user.model.User
import uniffi.wysiwyg_composer.ActionState
import uniffi.wysiwyg_composer.ComposerAction

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

        binding.ivRemoveImage.setOnClickListener {
            setTextContent()
        }
        updateContentView()

        with(binding.etTextPost) {
            doAfterTextChanged {
                listener?.onPostContentAvailable(it?.toString()?.isNotBlank() == true)
            }
            setShadowLayer(paddingBottom.toFloat(), 0f, 0f, 0)
            disallowParentInterceptTouchEvent(this)
//            linkDisplayHandler = LinkDisplayHandler { text, url ->
//                //pillDisplayHandler?.resolveLinkDisplay(text, url) ?: TextDisplay.Plain
//            }
        }
        setupRichTextMenu()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        binding.etTextPost.actionStatesChangedListener =
            EditorEditText.OnActionStatesChangedListener { state ->
                for (action in state.keys) {
                    updateMenuStateFor(action, state)
                }
            }
    }

    fun setup(
        previewPostListener: PreviewPostListener,
        onHighlightTextStyle: (List<TextStyle>) -> Unit,
        roomId: String
    ) {
        listener = previewPostListener
        // binding.etTextPost.setHighlightSelectedSpanListener(onHighlightTextStyle)
        //binding.etTextPost.initMentionsAutocomplete(roomId)
    }

    fun setText(message: String) {
        binding.etTextPost.setText(
            MarkdownParser.markwonBuilder(context).toMarkdown(message),
            TextView.BufferType.SPANNABLE
        )
        setTextContent()
    }

    fun setTextStyle(style: TextStyle, isSelected: Boolean) {
        // binding.etTextPost.triggerStyle(style, isSelected)
    }

    fun insertEmoji(unicode: String) {
        binding.etTextPost.text?.insert(binding.etTextPost.selectionStart, unicode)
    }

    fun insertMention() {
        binding.etTextPost.text?.insert(binding.etTextPost.selectionStart, "@")
    }

    fun insertLink(title: String?, link: String) {
        binding.etTextPost.insertLink(link, title ?: link)
    }

    fun setMediaFromExistingPost(mediaContent: MediaContent) {
        canEditMedia = false
        val caption = mediaContent.caption ?: ""
        setText(caption)
        val uri = Uri.parse(mediaContent.mediaFileData.fileUrl)
        val mediaType = mediaContent.getMediaType()
        postContent = MediaPostContent(caption, uri, mediaType)
        updateContentView()
        loadMediaCover(mediaContent)
        val isVideo = mediaType == MediaType.Video
        binding.lMediaContent.videoGroup.setIsVisible(isVideo)
        if (isVideo)
            binding.lMediaContent.tvDuration.text = mediaContent.mediaFileData.duration

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
        caption = binding.etTextPost.getMarkdown().trim().takeIf { it.isNotEmpty() }
    ) ?: TextPostContent(binding.etTextPost.getMarkdown().trim())

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
        listener?.onPostContentAvailable(false)
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
            val size = mediaContent.thumbnailOrFullSize(image.width)
            image.updateLayoutParams {
                width = size.width
                height = size.height
            }
        }
        mediaContent.loadEncryptedThumbOrFullIntoWithAspect(image)
    }

    private fun requestFocusOnText() {
        with(binding.etTextPost) {
            this.post {
                showKeyboard(true)
                text?.length?.let { setSelection(it) }
            }
        }
    }

    private fun getMyUser(): User? {
        val session = MatrixSessionProvider.currentSession
        return session?.myUserId?.let { session.getUser(it) }
    }

    private fun setupRichTextMenu() {
        addRichTextMenuItem(
            R.drawable.ic_bold,
            R.string.rich_text_editor_format_bold,
            ComposerAction.BOLD
        ) {
            binding.etTextPost.toggleInlineFormat(InlineFormat.Bold)
        }
        addRichTextMenuItem(
            R.drawable.ic_italic,
            R.string.rich_text_editor_format_italic,
            ComposerAction.ITALIC
        ) {
            binding.etTextPost.toggleInlineFormat(InlineFormat.Italic)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_underlined,
            R.string.rich_text_editor_format_underline,
            ComposerAction.UNDERLINE
        ) {
            binding.etTextPost.toggleInlineFormat(InlineFormat.Underline)
        }
        addRichTextMenuItem(
            R.drawable.ic_strikethrough,
            R.string.rich_text_editor_format_strikethrough,
            ComposerAction.STRIKE_THROUGH
        ) {
            binding.etTextPost.toggleInlineFormat(InlineFormat.StrikeThrough)
        }
        addRichTextMenuItem(
            R.drawable.ic_bullet_list,
            R.string.rich_text_editor_bullet_list,
            ComposerAction.UNORDERED_LIST
        ) {
            binding.etTextPost.toggleList(ordered = false)
        }
        addRichTextMenuItem(
            R.drawable.ic_number_list,
            R.string.rich_text_editor_numbered_list,
            ComposerAction.ORDERED_LIST
        ) {
            binding.etTextPost.toggleList(ordered = true)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_indent,
            R.string.rich_text_editor_indent,
            ComposerAction.INDENT
        ) {
            binding.etTextPost.indent()
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_unindent,
            R.string.rich_text_editor_unindent,
            ComposerAction.UNINDENT
        ) {
            binding.etTextPost.unindent()
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_quote,
            R.string.rich_text_editor_quote,
            ComposerAction.QUOTE
        ) {
            binding.etTextPost.toggleQuote()
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_code_block,
            R.string.rich_text_editor_code_block,
            ComposerAction.CODE_BLOCK
        ) {
            binding.etTextPost.toggleCodeBlock()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun disallowParentInterceptTouchEvent(view: View) {
        view.setOnTouchListener { v, event ->
            if (v.hasFocus()) {
                v.parent?.requestDisallowInterceptTouchEvent(true)
                val action = event.actionMasked
                if (action == MotionEvent.ACTION_SCROLL) {
                    v.parent?.requestDisallowInterceptTouchEvent(false)
                    return@setOnTouchListener true
                }
            }
            false
        }
    }

    private fun addRichTextMenuItem(
        @DrawableRes iconId: Int,
        @StringRes description: Int,
        action: ComposerAction,
        onClick: () -> Unit
    ) {
        val inflater = LayoutInflater.from(context)
        val item = ViewRichTextMenuButtonBinding.inflate(inflater, binding.textMenu, true)
        item.root.tag = action
        with(item.root) {
            contentDescription = resources.getString(description)
            item.ivIcon.setImageResource(iconId)
            setOnClickListener { onClick() }
        }
    }

    private fun updateMenuStateFor(
        action: ComposerAction,
        menuState: Map<ComposerAction, ActionState>
    ) {
        val button = findViewWithTag<CardView>(action) ?: return
        val stateForAction = menuState[action]
        button.isEnabled = stateForAction != ActionState.DISABLED
        button.isSelected = stateForAction == ActionState.REVERSED

        if (action == ComposerAction.INDENT || action == ComposerAction.UNINDENT) {
            val indentationButtonIsVisible =
                menuState[ComposerAction.ORDERED_LIST] == ActionState.REVERSED ||
                        menuState[ComposerAction.UNORDERED_LIST] == ActionState.REVERSED
            button.isVisible = indentationButtonIsVisible
        }
    }
}