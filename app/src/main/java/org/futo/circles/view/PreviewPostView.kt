package org.futo.circles.view


import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.inputmethod.InputMethodManager
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
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
import org.futo.circles.core.feature.autocomplete.Autocomplete
import org.futo.circles.core.feature.autocomplete.AutocompleteCallback
import org.futo.circles.core.feature.autocomplete.CharPolicy
import org.futo.circles.core.feature.markdown.mentions.MentionsLinkDisplayHandler
import org.futo.circles.core.feature.markdown.mentions.MentionsPresenter
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.UserListItem
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.ImageUtils
import org.futo.circles.core.utils.VideoUtils
import org.futo.circles.core.utils.VideoUtils.getVideoDuration
import org.futo.circles.core.utils.VideoUtils.getVideoDurationString
import org.futo.circles.databinding.ViewPreviewPostBinding
import org.futo.circles.databinding.ViewRichTextMenuButtonBinding
import org.futo.circles.extensions.convertDpToPixel
import org.futo.circles.feature.timeline.post.create.PreviewPostListener
import org.futo.circles.model.CreatePostContent
import org.futo.circles.model.MediaPostContent
import org.futo.circles.model.TextPostContent
import org.matrix.android.sdk.api.session.getUser
import org.matrix.android.sdk.api.session.permalinks.PermalinkService.Companion.MATRIX_TO_URL_BASE
import org.matrix.android.sdk.api.session.user.model.User
import uniffi.wysiwyg_composer.ActionState
import uniffi.wysiwyg_composer.ComposerAction

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
        binding.ivRemoveImage.setOnClickListener { setTextContent() }
        updateContentView()

        binding.btnSend.setOnClickListener { listener?.onSendClicked(getPostContent()) }
        binding.ivCancel.setOnClickListener { setTextEditorMode(false) }

        with(binding.etTextPost) {
            doAfterTextChanged {
                binding.btnSend.isEnabled = it?.toString()?.isNotBlank() == true
            }
            updateStyle(
                styleConfig = this.styleConfig,
                mentionDisplayHandler = MentionsLinkDisplayHandler(context)
            )
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

    override fun onDetachedFromWindow() {
        binding.etTextPost.setMarkdown("")
        super.onDetachedFromWindow()
    }

    fun setup(
        previewPostListener: PreviewPostListener,
        roomId: String,
        isEdit: Boolean
    ) {
        setTextEditorMode(false)
        listener = previewPostListener
        setupMainMenu(!isEdit)
        initMentionsAutocomplete(roomId)
    }

    fun setText(message: String) {
        binding.etTextPost.setFormattedMarkdown(message)
        setTextContent()
    }

    fun insertEmoji(unicode: String) {
        val selection = binding.etTextPost.selectionStart
        binding.etTextPost.append(unicode)
        binding.etTextPost.setSelection(selection + unicode.length)
    }

    private fun insertMentionMark() {
        with(binding.etTextPost) {
            val selection = selectionStart
            val previousChar = text?.getOrNull(selection - 1)
            val noNeedToAddSpace = previousChar?.isWhitespace() == true || previousChar == null
            val textToInsert = if (noNeedToAddSpace) "@" else " @"
            append(textToInsert)
            setSelection(selection + textToInsert.length)
        }
    }

    fun insertLink(title: String?, link: String) {
        val selection = binding.etTextPost.selectionStart
        val text = title ?: link
        binding.etTextPost.insertLink(link, text)
        binding.etTextPost.setSelection(selection + text.length)
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

        binding.btnSend.isEnabled = true
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

        binding.btnSend.isEnabled = true
    }

    private fun getPostContent() = (postContent as? MediaPostContent)?.copy(
        caption = binding.etTextPost.getFormattedMarkdown().takeIf { it.isNotEmpty() }
    ) ?: TextPostContent(binding.etTextPost.getFormattedMarkdown())

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
        binding.btnSend.isEnabled = false
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

    private fun initMentionsAutocomplete(roomId: String) {
        Autocomplete.on<UserListItem>(binding.etTextPost)
            .with(CharPolicy('@', true))
            .with(MentionsPresenter(context, roomId))
            .with(
                ColorDrawable(
                    ContextCompat.getColor(
                        context,
                        org.futo.circles.core.R.color.post_card_background_color
                    )
                )
            )
            .with(object : AutocompleteCallback<UserListItem> {
                override fun onPopupItemClicked(editable: Editable, item: UserListItem): Boolean {
                    binding.etTextPost.insertMentionAtSuggestion(
                        MATRIX_TO_URL_BASE + item.id,
                        "@${item.user.name}@"
                    )
                    return true
                }

                override fun onPopupVisibilityChanged(shown: Boolean) {
                }
            })
            .with(6f)
            .build()
    }

    private fun requestFocusOnText() {
        binding.etTextPost.post {
            requestFocus()
            binding.etTextPost.text?.let { binding.etTextPost.setSelection(it.length) }
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

    private fun setupMainMenu(isMediaAvailable: Boolean) {
        if (isMediaAvailable) {
            addMenuItem(binding.lMainMenu, R.drawable.ic_image) {
                listener?.onUploadMediaClicked()
            }
        }
        addMenuItem(binding.lMainMenu, R.drawable.ic_emoji) {
            listener?.onEmojiClicked()
        }
        addMenuItem(binding.lMainMenu, org.futo.circles.core.R.drawable.ic_mention) {
            insertMentionMark()
        }
        addMenuItem(binding.lMainMenu, org.futo.circles.core.R.drawable.ic_link) {
            listener?.onAddLinkClicked()
        }
        addMenuItem(binding.lMainMenu, R.drawable.ic_text) {
            setTextEditorMode(true)
        }
    }

    private fun setTextEditorMode(isEnabled: Boolean) {
        binding.ivCancel.setIsVisible(isEnabled)
        binding.lTextMenu.setIsVisible(isEnabled)
        binding.lMainMenu.setIsVisible(!isEnabled)
    }

    private fun setupRichTextMenu() {
        addMenuItem(
            binding.lTextMenu,
            R.drawable.ic_bold,
            ComposerAction.BOLD
        ) {
            binding.etTextPost.toggleInlineFormat(InlineFormat.Bold)
        }
        addMenuItem(
            binding.lTextMenu,
            R.drawable.ic_italic,
            ComposerAction.ITALIC
        ) {
            binding.etTextPost.toggleInlineFormat(InlineFormat.Italic)
        }
        addMenuItem(
            binding.lTextMenu,
            R.drawable.ic_strikethrough,
            ComposerAction.STRIKE_THROUGH
        ) {
            binding.etTextPost.toggleInlineFormat(InlineFormat.StrikeThrough)
        }
        addMenuItem(
            binding.lTextMenu,
            R.drawable.ic_bullet_list,
            ComposerAction.UNORDERED_LIST
        ) {
            binding.etTextPost.toggleList(ordered = false)
        }
        addMenuItem(
            binding.lTextMenu,
            R.drawable.ic_number_list,
            ComposerAction.ORDERED_LIST
        ) {
            binding.etTextPost.toggleList(ordered = true)
        }
        addMenuItem(
            binding.lTextMenu,
            R.drawable.ic_composer_quote,
            ComposerAction.QUOTE
        ) {
            binding.etTextPost.toggleQuote()
        }
    }

    private fun addMenuItem(
        container: LinearLayout,
        @DrawableRes iconId: Int,
        action: ComposerAction? = null,
        onClick: () -> Unit
    ) {
        val inflater = LayoutInflater.from(context)
        val item = ViewRichTextMenuButtonBinding.inflate(inflater, container, true)
        action?.let {
            item.root.tag = it
        }
        with(item.root) {
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

    //To remove extra space between word and markdown char (__aaa __*b* -> __aaa__ *b*)
    private fun EditorEditText.getFormattedMarkdown(): String = getMarkdown()
        .replace("__", "**")
        .replace(Regex("\\*\\*(\\S+(?:\\s\\S+)*) \\*\\*"), "**$1** ")
        .replace(Regex("\\*(\\S+(?:\\s\\S+)*) \\*"), "*$1* ")
        .replace(Regex("~~(\\S+(?:\\s\\S+)*) ~~"), "~~$1~~ ")

    // __ does not work for inline like: asd__asd__asd
    private fun EditorEditText.setFormattedMarkdown(message: String) {
        val formattedMessage = message
            .replace("__", "**")
        setMarkdown(formattedMessage)
    }

}