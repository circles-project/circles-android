package org.futo.circles.view


import android.annotation.SuppressLint
import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import io.element.android.wysiwyg.EditorEditText
import io.element.android.wysiwyg.display.KeywordDisplayHandler
import io.element.android.wysiwyg.display.LinkDisplayHandler
import io.element.android.wysiwyg.display.TextDisplay
import io.element.android.wysiwyg.view.models.InlineFormat
import io.element.android.wysiwyg.view.models.LinkAction
import org.futo.circles.R
import org.futo.circles.databinding.ComposerRichTextLayoutBinding
import org.futo.circles.databinding.ViewRichTextMenuButtonBinding
import org.futo.circles.extensions.setTextIfDifferent
import uniffi.wysiwyg_composer.ActionState
import uniffi.wysiwyg_composer.ComposerAction

internal class RichTextComposerLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), MessageComposerView {

    private val views: ComposerRichTextLayoutBinding

    override var callback: Callback? = null

    override val text: Editable?
        get() = editText.text
    override val formattedText: String?
        get() = (editText as? EditorEditText)?.getContentAsMessageHtml()
    override val editText: EditText
        get() = views.richTextComposerEditText

    override val emojiButton: ImageButton?
        get() = null

    var pillDisplayHandler: PillDisplayHandler? = null

    init {
        inflate(context, R.layout.composer_rich_text_layout, this)
        views = ComposerRichTextLayoutBinding.bind(this)

        // Workaround to avoid cut-off text caused by padding in scrolled TextView (there is no clipToPadding).
        // In TextView, clipTop = padding, but also clipTop -= shadowRadius. So if we set the shadowRadius to padding, they cancel each other
        views.richTextComposerEditText.setShadowLayer(
            views.richTextComposerEditText.paddingBottom.toFloat(),
            0f,
            0f,
            0
        )

        views.richTextComposerEditText.doOnTextChanged { s, _, _, _ ->
            callback?.onTextChanged(s ?: "")
        }

        disallowParentInterceptTouchEvent(views.richTextComposerEditText)

        setupRichTextMenu()
        views.richTextComposerEditText.linkDisplayHandler = LinkDisplayHandler { text, url ->
            pillDisplayHandler?.resolveLinkDisplay(text, url) ?: TextDisplay.Plain
        }
        views.richTextComposerEditText.keywordDisplayHandler = object : KeywordDisplayHandler {
            override val keywords: List<String>
                get() = pillDisplayHandler?.keywords.orEmpty()

            override fun resolveKeywordDisplay(text: String): TextDisplay =
                pillDisplayHandler?.resolveKeywordDisplay(text) ?: TextDisplay.Plain
        }
    }

    private fun setupRichTextMenu() {
        addRichTextMenuItem(
            R.drawable.ic_composer_bold,
            R.string.rich_text_editor_format_bold,
            ComposerAction.BOLD
        ) {
            views.richTextComposerEditText.toggleInlineFormat(InlineFormat.Bold)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_italic,
            R.string.rich_text_editor_format_italic,
            ComposerAction.ITALIC
        ) {
            views.richTextComposerEditText.toggleInlineFormat(InlineFormat.Italic)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_underlined,
            R.string.rich_text_editor_format_underline,
            ComposerAction.UNDERLINE
        ) {
            views.richTextComposerEditText.toggleInlineFormat(InlineFormat.Underline)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_strikethrough,
            R.string.rich_text_editor_format_strikethrough,
            ComposerAction.STRIKE_THROUGH
        ) {
            views.richTextComposerEditText.toggleInlineFormat(InlineFormat.StrikeThrough)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_bullet_list,
            R.string.rich_text_editor_bullet_list,
            ComposerAction.UNORDERED_LIST
        ) {
            views.richTextComposerEditText.toggleList(ordered = false)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_numbered_list,
            R.string.rich_text_editor_numbered_list,
            ComposerAction.ORDERED_LIST
        ) {
            views.richTextComposerEditText.toggleList(ordered = true)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_indent,
            R.string.rich_text_editor_indent,
            ComposerAction.INDENT
        ) {
            views.richTextComposerEditText.indent()
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_unindent,
            R.string.rich_text_editor_unindent,
            ComposerAction.UNINDENT
        ) {
            views.richTextComposerEditText.unindent()
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_quote,
            R.string.rich_text_editor_quote,
            ComposerAction.QUOTE
        ) {
            views.richTextComposerEditText.toggleQuote()
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_inline_code,
            R.string.rich_text_editor_inline_code,
            ComposerAction.INLINE_CODE
        ) {
            views.richTextComposerEditText.toggleInlineFormat(InlineFormat.InlineCode)
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_code_block,
            R.string.rich_text_editor_code_block,
            ComposerAction.CODE_BLOCK
        ) {
            views.richTextComposerEditText.toggleCodeBlock()
        }
        addRichTextMenuItem(
            R.drawable.ic_composer_link,
            R.string.rich_text_editor_link,
            ComposerAction.LINK
        ) {
            views.richTextComposerEditText.getLinkAction()?.let {
                when (it) {
                    LinkAction.InsertLink -> callback?.onSetLink(
                        isTextSupported = true,
                        initialLink = null
                    )

                    is LinkAction.SetLink -> callback?.onSetLink(
                        isTextSupported = false,
                        initialLink = it.currentUrl
                    )
                }
            }
        }
    }

    fun setLink(link: String?) =
        views.richTextComposerEditText.setLink(link)

    fun insertLink(link: String, text: String) =
        views.richTextComposerEditText.insertLink(link, text)

    fun removeLink() =
        views.richTextComposerEditText.removeLink()

    fun insertMention(url: String, displayText: String) =
        views.richTextComposerEditText.insertLink(url, displayText)

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

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()

        views.richTextComposerEditText.actionStatesChangedListener =
            EditorEditText.OnActionStatesChangedListener { state ->
                for (action in state.keys) {
                    updateMenuStateFor(action, state)
                }
            }
    }


    private fun addRichTextMenuItem(
        @DrawableRes iconId: Int,
        @StringRes description: Int,
        action: ComposerAction,
        onClick: () -> Unit
    ) {
        val inflater = LayoutInflater.from(context)
        val button = ViewRichTextMenuButtonBinding.inflate(inflater, views.richTextMenu, true)
        button.root.tag = action
        with(button.root) {
            contentDescription = resources.getString(description)
            setImageResource(iconId)
            setOnClickListener {
                onClick()
            }
        }
    }

    private fun updateMenuStateFor(
        action: ComposerAction,
        menuState: Map<ComposerAction, ActionState>
    ) {
        val button = findViewWithTag<ImageButton>(action) ?: return
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

    override fun setTextIfDifferent(text: CharSequence?): Boolean {
        return editText.setTextIfDifferent(text)
    }

}
