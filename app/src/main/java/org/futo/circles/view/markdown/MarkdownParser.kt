package org.futo.circles.view.markdown

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.style.StrikethroughSpan
import android.text.util.Linkify
import androidx.core.content.ContextCompat
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.core.spans.BulletListItemSpan
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListItem
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.ext.tasklist.TaskListSpan
import io.noties.markwon.linkify.LinkifyPlugin
import org.commonmark.node.SoftLineBreak
import org.futo.circles.R
import org.futo.circles.extensions.getGivenSpansAt


object MarkdownParser {

    private const val boldMark = "**"
    private const val italicMark = "_"
    private const val strikeMark = "~~"
    private const val notDoneMark = "* [ ]"
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

    fun markdownToEditable(markdown: String, context: Context): Editable {
        return Editable.Factory.getInstance()
            .newEditable(markwonBuilder(context).toMarkdown(markdown))
    }

    fun markwonBuilder(context: Context): Markwon = Markwon.builder(context)
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(
            TaskListPlugin.create(
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.blue),
                Color.WHITE
            )
        )
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
                    (span as? TaskListSpan)?.let { arrayOf(span) }
                }
            }
        }).build()

}