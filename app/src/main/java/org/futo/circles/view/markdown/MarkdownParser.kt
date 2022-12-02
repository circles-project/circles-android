package org.futo.circles.view.markdown

import android.text.Editable
import android.text.Spannable
import android.text.style.StrikethroughSpan
import io.noties.markwon.core.spans.BulletListItemSpan
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.ext.tasklist.TaskListSpan
import org.futo.circles.extensions.getGivenSpansAt


object MarkdownParser {

    private const val boldMark = "**"
    private const val italicMark = "_"
    private const val strikeMark = "~~"
    private const val notDoneMark = "* []"
    private const val doneMark = "* [x]"


    fun editableToMarkdown(text: Editable): String {
        val textCopy = Editable.Factory.getInstance().newEditable(text)
        text.getGivenSpansAt(span = TextStyle.values()).forEach {
            val start = textCopy.getSpanStart(it)
            val end = textCopy.getSpanEnd(it)
            when (it) {
                is StrongEmphasisSpan -> {
                    textCopy.insert(start, boldMark)
                    textCopy.insert(end + boldMark.length, boldMark)
                }
                is EmphasisSpan -> {
                    textCopy.insert(start, italicMark)
                    textCopy.insert(end + italicMark.length, italicMark)
                }
                is StrikethroughSpan -> {
                    textCopy.insert(start, strikeMark)
                    textCopy.insert(end + strikeMark.length, strikeMark)
                }
                is LinkSpan -> {
                    val linkStartMark = "["
                    textCopy.insert(start, linkStartMark)
                    textCopy.insert(end + linkStartMark.length, "](${it.link})")
                }
                is BulletListItemSpan -> textCopy.insert(start, "*")
                is OrderedListItemSpan -> textCopy.insert(start, it.number)
                is TaskListSpan -> {
                    val taskSpanMark = if (it.isDone) doneMark else notDoneMark
                    textCopy.insert(start, taskSpanMark)
                }
            }
        }
        return textCopy.toString()
    }

    fun markdownToEditable(markdown: String): Editable {
        val editable = Editable.Factory.getInstance().newEditable(markdown)
        setTextStyleSpan(editable, boldMark, StrongEmphasisSpan())
        setTextStyleSpan(editable, italicMark, EmphasisSpan())
        setTextStyleSpan(editable, strikeMark, StrikethroughSpan())
        return editable
    }

    private fun setTextStyleSpan(editable: Editable, mark: String, span: Any) {
        var pointer = 0
        while (pointer <= editable.lastIndex) {
            val start = editable.indexOf(mark, pointer)
            val end = editable.indexOf(mark, start + mark.length)
            if (start == -1 || end == -1) break
            editable.setSpan(span, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            pointer = end + mark.length
        }
        while (true) {
            val index = editable.indexOf(mark, 0)
            if (index == -1) break
            editable.replace(index, index + mark.length, "")
        }

    }
}