package org.futo.circles.view.markdown


import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.TextWatcher
import android.text.style.ClickableSpan
import android.text.style.StrikethroughSpan
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import io.noties.markwon.*
import io.noties.markwon.core.spans.BulletListItemSpan
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListDrawable
import io.noties.markwon.ext.tasklist.TaskListItem
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.ext.tasklist.TaskListSpan
import org.commonmark.node.SoftLineBreak
import org.futo.circles.R
import org.futo.circles.extensions.getGivenSpansAt

class MarkdownEditText(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatEditText(context, attrs) {

    private val markwon: Markwon
    private var isSelectionStyling = false
    private var listSpanStart = 0
    private val textWatchers: MutableList<TextWatcher> = mutableListOf()
    private val taskBoxColor by lazy { ContextCompat.getColor(context, R.color.blue) }
    private val taskBoxMarkColor = Color.WHITE
    private var onHighlightSpanListener: ((List<TextStyle>) -> Unit)? = null

    init {
        markwon = markwonBuilder(context)
    }

    override fun getText(): Editable {
        return super.getText() ?: Editable.Factory.getInstance().newEditable("")
    }

    fun setHighlightSelectedSpanListener(onHighlight: (List<TextStyle>) -> Unit) {
        onHighlightSpanListener = onHighlight
    }

    fun insertMention() {
        insertText("@")
    }

    fun insertText(message: String) {
        text.insert(selectionStart, message)
    }

    private fun markwonBuilder(context: Context): Markwon {
        movementMethod = EnhancedMovementMethod().getsInstance()
        return Markwon.builder(context)
            .usePlugin(StrikethroughPlugin.create())
            .usePlugin(TaskListPlugin.create(taskBoxColor, taskBoxColor, taskBoxMarkColor))
            .usePlugin(object : AbstractMarkwonPlugin() {
                override fun configureVisitor(builder: MarkwonVisitor.Builder) {
                    super.configureVisitor(builder)
                    builder.on(SoftLineBreak::class.java) { visitor, _ -> visitor.forceNewLine() }
                }

                override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                    val origin = builder.getFactory(TaskListItem::class.java)
                    builder.setFactory(
                        TaskListItem::class.java
                    ) { configuration, props ->
                        val span = origin?.getSpans(configuration, props)
                        (span as? TaskListSpan)?.let { arrayOf(span, getTaskClickableSpan(span)) }
                    }
                }
            }).build()
    }

    fun triggerStyle(textStyle: TextStyle, stop: Boolean) {
        if (stop) {
            clearTextWatchers()
        } else {
            when (textStyle) {
                TextStyle.UNORDERED_LIST, TextStyle.ORDERED_LIST, TextStyle.TASKS_LIST ->
                    triggerListStyle(textStyle)
                else -> {
                    if (isSelectionStyling) {
                        styliseText(textStyle, selectionStart, selectionEnd)
                        isSelectionStyling = false
                    } else {
                        addTextWatcher(doOnTextChanged { _, start, before, count ->
                            if (before < count) styliseText(textStyle, start)
                        })
                    }
                }
            }
        }
    }

    private fun triggerListStyle(listSpanStyle: TextStyle) {
        var currentNum = 1
        val currentLineStart = layout.getLineStart(getCurrentCursorLine())
        if (text.length < currentLineStart + 1 || text.getGivenSpansAt(
                span = arrayOf(listSpanStyle), currentLineStart, currentLineStart + 1
            ).isEmpty()
        ) {
            if (text.isNotEmpty()) {
                val otherListSpans = mutableListOf(
                    TextStyle.ORDERED_LIST,
                    TextStyle.UNORDERED_LIST,
                    TextStyle.TASKS_LIST
                ).apply { remove(listSpanStyle) }.toTypedArray()
                if (text.length > 1 && text.getGivenSpansAt(
                        span = otherListSpans, selectionStart - 2, selectionStart
                    ).isEmpty()
                ) {
                    if (text.toString().substring(text.length - 2, text.length) != "\n") {
                        text.insert(selectionStart, "\n ")
                    } else {
                        text.insert(selectionStart, " ")
                    }
                } else {
                    text.insert(selectionStart, "\n ")
                }

            } else {
                text.insert(selectionStart, " ")
            }

            listSpanStart = selectionStart - 1
            text.setSpan(
                getListSpan(listSpanStyle, "${currentNum}.", false),
                listSpanStart,
                selectionStart,
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        currentNum++
        var mLineCount = lineCount
        addTextWatcher(doOnTextChanged { _, _, before, count ->
            if (before < count) {
                if (selectionStart == selectionEnd && mLineCount < lineCount) {
                    mLineCount = lineCount
                    val string = text.toString()
                    // If user hit enter
                    if (string[selectionStart - 1] == '\n') {
                        listSpanStart = selectionStart
                        text.insert(selectionStart, " ")
                        text.setSpan(
                            getListSpan(listSpanStyle, "${currentNum}.", false),
                            listSpanStart,
                            listSpanStart + 1,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                        currentNum++
                    } else {
                        for (bulletSpan in text.getGivenSpansAt(
                            span = arrayOf(listSpanStyle),
                            listSpanStart,
                            listSpanStart + 1
                        )) {
                            val number = (bulletSpan as? OrderedListItemSpan)?.number ?: ""
                            val isDone = (bulletSpan as? TaskListSpan)?.isDone ?: false
                            text.removeSpan(bulletSpan)
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
        })
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

    private fun styliseText(
        textStyle: TextStyle,
        start: Int,
        end: Int? = null
    ) {
        val endIndex = end ?: (start + 1)
        val span = when (textStyle) {
            TextStyle.BOLD -> StrongEmphasisSpan()
            TextStyle.ITALIC -> EmphasisSpan()
            TextStyle.STRIKE -> StrikethroughSpan()
            else -> null
        }
        span?.let {
            text.setSpan(it, start, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }

    override fun onSelectionChanged(selStart: Int, selEnd: Int) {
        super.onSelectionChanged(selStart, selEnd)
        if (selStart != selEnd) isSelectionStyling = true
        if (selStart <= 0) {
            onHighlightSpanListener?.invoke(emptyList())
            return
        }
        val spans = mutableListOf<TextStyle>()
        val currentLineStart = layout.getLineStart(getCurrentCursorLine())
        val listsSpans = text.getGivenSpansAt(
            span = arrayOf(
                TextStyle.UNORDERED_LIST,
                TextStyle.TASKS_LIST,
                TextStyle.ORDERED_LIST
            ),
            start = currentLineStart, end = currentLineStart + 1
        )
        listsSpans.forEach {
            when (it) {
                is BulletListItemSpan -> spans.add(TextStyle.UNORDERED_LIST)
                is OrderedListItemSpan -> spans.add(TextStyle.ORDERED_LIST)
                is TaskListSpan -> spans.add(TextStyle.TASKS_LIST)
            }
        }
        val textSpans = text.getGivenSpansAt(
            span = arrayOf(TextStyle.BOLD, TextStyle.ITALIC, TextStyle.STRIKE),
            start = selStart - 1, end = selStart
        )
        textSpans.forEach {
            when (it) {
                is StrongEmphasisSpan -> spans.add(TextStyle.BOLD)
                is EmphasisSpan -> spans.add(TextStyle.ITALIC)
                is StrikethroughSpan -> spans.add(TextStyle.STRIKE)
            }
        }
        onHighlightSpanListener?.invoke(spans)
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

    private fun addTextWatcher(textWatcher: TextWatcher) {
        textWatchers.add(textWatcher)
    }

    private fun clearTextWatchers() {
        textWatchers.forEach { removeTextChangedListener(it) }
    }

    private fun getCurrentCursorLine(): Int {
        return if (selectionStart != -1) layout.getLineForOffset(selectionStart) else -1
    }

    fun getTextWithMarkdown(): String {
        clearTextWatchers()
        val textCopy = Editable.Factory.getInstance().newEditable(text)
        text.getGivenSpansAt(span = TextStyle.values()).forEach {
            val start = textCopy.getSpanStart(it)
            val end = textCopy.getSpanEnd(it)
            when (it) {
                is StrongEmphasisSpan -> {
                    val boldMark = "**"
                    textCopy.insert(start, boldMark)
                    textCopy.insert(end + boldMark.length, boldMark)
                }
                is EmphasisSpan -> {
                    val italicMark = "_"
                    textCopy.insert(start, italicMark)
                    textCopy.insert(end + italicMark.length, italicMark)
                }
                is StrikethroughSpan -> {
                    val strikeMark = "~~"
                    textCopy.insert(start, strikeMark)
                    textCopy.insert(end + strikeMark.length, strikeMark)
                }
                is LinkSpan -> {
                    val linkStartMark = "["
                    textCopy.insert(start, linkStartMark)
                    textCopy.insert(end + linkStartMark.length, "](${it.link})")
                }
                is BulletListItemSpan -> {

                }
                is OrderedListItemSpan -> {

                }
                is TaskListSpan -> {}
            }
        }
        return textCopy.toString()
    }
}