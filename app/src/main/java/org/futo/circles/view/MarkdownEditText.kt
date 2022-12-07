package org.futo.circles.view


import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.StrikethroughSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import com.otaliastudios.autocomplete.Autocomplete
import com.otaliastudios.autocomplete.AutocompleteCallback
import com.otaliastudios.autocomplete.CharPolicy
import io.noties.markwon.LinkResolverDef
import io.noties.markwon.Markwon
import io.noties.markwon.core.spans.BulletListItemSpan
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.ext.tasklist.TaskListDrawable
import io.noties.markwon.ext.tasklist.TaskListSpan
import org.futo.circles.R
import org.futo.circles.extensions.getGivenSpansAt
import org.futo.circles.feature.timeline.post.markdown.EnhancedMovementMethod
import org.futo.circles.feature.timeline.post.markdown.MarkdownParser
import org.futo.circles.feature.timeline.post.markdown.mentions.MentionsPresenter
import org.futo.circles.feature.timeline.post.markdown.span.OrderedListItemSpan
import org.futo.circles.feature.timeline.post.markdown.span.TextStyle
import org.futo.circles.model.UserListItem

class MarkdownEditText(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val markwon: Markwon
    private var isSelectionStyling = false
    private var listSpanStart = 0
    private var currentListSpanNumber = 0
    private var currentListSpanLine = 0
    private val taskBoxColor by lazy { ContextCompat.getColor(context, R.color.blue) }
    private val taskBoxMarkColor = Color.WHITE
    private val textStyles =
        arrayOf(TextStyle.BOLD, TextStyle.ITALIC, TextStyle.STRIKE)
    private val listStyles =
        arrayOf(TextStyle.UNORDERED_LIST, TextStyle.ORDERED_LIST, TextStyle.TASKS_LIST)
    private var onHighlightSpanListener: ((List<TextStyle>) -> Unit)? = null
    private val selectedStyles = mutableSetOf<TextStyle>()

    init {
        movementMethod = EnhancedMovementMethod().getsInstance()
        markwon = MarkdownParser.markwonBuilder(context)
        doOnTextChanged { _, start, before, count ->
            styliseText(start, start + count)
            handleListSpanTextChange(before, count)
        }
    }

    override fun getText(): Editable {
        return super.getText() ?: Editable.Factory.getInstance().newEditable("")
    }

    fun getTextWithMarkdown() = MarkdownParser.editableToMarkdown(text)

    fun setHighlightSelectedSpanListener(onHighlight: (List<TextStyle>) -> Unit) {
        onHighlightSpanListener = onHighlight
    }

    fun insertMention() {
        insertText(MarkdownParser.mentionMark)
    }

    fun insertText(message: String) {
        text.insert(selectionStart, message)
    }

    fun triggerStyle(textStyle: TextStyle, isSelected: Boolean) {
        if (isSelected) {
            selectOnlyOneListStyleIfNeed(textStyle)
            selectedStyles.add(textStyle)
        } else selectedStyles.remove(textStyle)
        handleSelectionStylingIfNeed()
        onHighlightSpanListener?.invoke(selectedStyles.toList())
    }

    fun initMentionsAutocomplete(roomId: String) {
        Autocomplete.on<UserListItem>(this)
            .with(CharPolicy('@'))
            .with(MentionsPresenter(context, roomId))
            .with(
                ColorDrawable(ContextCompat.getColor(context, R.color.post_card_background_color))
            )
            .with(object : AutocompleteCallback<UserListItem> {
                override fun onPopupItemClicked(editable: Editable, item: UserListItem): Boolean {
                    return true
                }

                override fun onPopupVisibilityChanged(shown: Boolean) {
                }
            })
            .with(6f)
            .build()
    }

    private fun handleSelectionStylingIfNeed() {
        if (!isSelectionStyling) return
        text.getGivenSpansAt(span = textStyles, selectionStart, selectionEnd).forEach {
            text.removeSpan(it)
        }
        styliseText(selectionStart, selectionEnd)
    }

    private fun selectOnlyOneListStyleIfNeed(textStyle: TextStyle) {
        if (textStyle !in listStyles) return
        selectedStyles.removeAll { it in listStyles }
        triggerListStyle(textStyle)
    }

    private fun triggerListStyle(listSpanStyle: TextStyle) {
        currentListSpanNumber = 1
        val currentLineStart = layout.getLineStart(getCurrentCursorLine())
        if (selectionStart == currentLineStart)
            text.insert(selectionStart, " ")
        else text.insert(selectionStart, "\n ")

        listSpanStart = selectionStart - 1
        text.setSpan(
            getListSpan(listSpanStyle, "${currentListSpanNumber}.", false),
            listSpanStart,
            selectionStart,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        currentListSpanNumber++
        currentListSpanLine = lineCount
    }

    private fun handleListSpanTextChange(before: Int, count: Int) {
        val listSpanStyle = selectedStyles.firstOrNull { it in listStyles } ?: return
        if (before > count) return
        if (selectionStart == selectionEnd && currentListSpanLine < lineCount) {
            currentListSpanLine = lineCount
            val string = text.toString()
            // If user hit enter
            if (string[selectionStart - 1] == '\n') {
                listSpanStart = selectionStart
                text.insert(selectionStart, " ")
                text.setSpan(
                    getListSpan(listSpanStyle, "${currentListSpanNumber}.", false),
                    listSpanStart,
                    listSpanStart + 1,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                currentListSpanNumber++
            } else {
                for (listSpan in text.getGivenSpansAt(
                    span = arrayOf(listSpanStyle),
                    listSpanStart,
                    listSpanStart + 1
                )) {
                    val number = (listSpan as? OrderedListItemSpan)?.number ?: ""
                    val isDone = (listSpan as? TaskListSpan)?.isDone ?: false
                    text.removeSpan(listSpan)
                    text.setSpan(
                        getListSpan(listSpanStyle, number, isDone),
                        listSpanStart,
                        selectionStart,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                }
            }
        }
    }

    private fun getListSpan(listSpanStyle: TextStyle, currentNum: String, isDone: Boolean): Any =
        when (listSpanStyle) {
            TextStyle.ORDERED_LIST -> OrderedListItemSpan(
                markwon.configuration().theme(),
                currentNum
            )
            TextStyle.TASKS_LIST -> setTaskSpan(listSpanStart, selectionStart, isDone)
            else -> BulletListItemSpan(markwon.configuration().theme(), 0)
        }

    fun addLinkSpan(title: String?, link: String) {
        val newTitle = if (title.isNullOrEmpty()) link else title
        val cursorStart = selectionStart
        text.insert(cursorStart, newTitle)
        text.setSpan(
            LinkSpan(markwon.configuration().theme(), link, LinkResolverDef()),
            cursorStart,
            cursorStart + newTitle.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }

    private fun setTaskSpan(start: Int, end: Int, isDone: Boolean) {
        val taskSpan = TaskListSpan(
            markwon.configuration().theme(),
            TaskListDrawable(taskBoxColor, taskBoxColor, taskBoxMarkColor),
            isDone
        )
        text.setSpan(taskSpan, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        text.setSpan(getTaskClickableSpan(taskSpan), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }

    private fun styliseText(start: Int, end: Int) {
        if (start >= end) return
        if (text.substring(start, end).isBlank()) return
        selectedStyles.forEach { textStyle ->
            val span = when (textStyle) {
                TextStyle.BOLD -> StrongEmphasisSpan()
                TextStyle.ITALIC -> EmphasisSpan()
                TextStyle.STRIKE -> StrikethroughSpan()
                else -> null
            }
            span?.let {
                if (text.getGivenSpansAt(span = arrayOf(textStyle), start, end).isEmpty())
                    text.setSpan(it, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        isSelectionStyling = selStart != selEnd
        if (selStart <= 0) return
        if (isDividerSymbol(selStart - 1) && !isSelectionStyling) return

        val spans = mutableSetOf<TextStyle>()
        val currentLineStart = layout.getLineStart(getCurrentCursorLine())
        val listsSpans = text.getGivenSpansAt(
            span = listStyles,
            start = currentLineStart, end = currentLineStart + 1
        )
        listsSpans.forEach {
            when (it) {
                is BulletListItemSpan -> spans.add(TextStyle.UNORDERED_LIST)
                is OrderedListItemSpan -> spans.add(TextStyle.ORDERED_LIST)
                is TaskListSpan -> spans.add(TextStyle.TASKS_LIST)
            }
        }
        val textStart = if (isSelectionStyling) selStart else selStart - 1
        val textEnd = if (isSelectionStyling) selEnd else selStart
        val textSpans = text.getGivenSpansAt(
            span = textStyles,
            start = textStart, end = textEnd
        )
        textSpans.forEach {
            when (it) {
                is StrongEmphasisSpan -> spans.add(TextStyle.BOLD)
                is EmphasisSpan -> spans.add(TextStyle.ITALIC)
                is StrikethroughSpan -> spans.add(TextStyle.STRIKE)
            }
        }
        if (spans != selectedStyles) {
            selectedStyles.clear()
            selectedStyles.addAll(spans)
        }
        onHighlightSpanListener?.invoke(spans.toList())
    }

    private fun isDividerSymbol(index: Int): Boolean {
        val char = text.getOrNull(index).toString()
        return char == " " || char == "\n"
    }

    private fun getTaskClickableSpan(taskSpan: TaskListSpan) = object : ClickableSpan() {
        override fun onClick(widget: View) {
            val spanStart = text.getSpanStart(taskSpan)
            val spanEnd = text.getSpanEnd(taskSpan)
            taskSpan.isDone = !taskSpan.isDone
            if (spanStart >= 0) {
                text.setSpan(taskSpan, spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }

    private fun getCurrentCursorLine(): Int {
        return if (selectionStart != -1) layout.getLineForOffset(selectionStart) else -1
    }
}