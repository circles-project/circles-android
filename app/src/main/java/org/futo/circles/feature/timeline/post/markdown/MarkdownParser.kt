package org.futo.circles.feature.timeline.post.markdown

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.spans.BulletListItemSpan
import io.noties.markwon.core.spans.EmphasisSpan
import io.noties.markwon.core.spans.LinkSpan
import io.noties.markwon.core.spans.StrongEmphasisSpan
import io.noties.markwon.ext.strikethrough.StrikethroughPlugin
import io.noties.markwon.ext.tasklist.TaskListPlugin
import io.noties.markwon.ext.tasklist.TaskListSpan
import io.noties.markwon.linkify.LinkifyPlugin
import org.commonmark.node.Emphasis
import org.commonmark.node.StrongEmphasis
import org.futo.circles.R
import org.futo.circles.extensions.getGivenSpansAt
import org.futo.circles.feature.timeline.post.markdown.mentions.plugin.MentionPlugin
import org.futo.circles.feature.timeline.post.markdown.span.MentionSpan
import org.futo.circles.feature.timeline.post.markdown.span.OrderedListItemSpan
import org.futo.circles.feature.timeline.post.markdown.span.TextStyle
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getUserOrDefault


object MarkdownParser {

    const val mentionMark = "@"
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
                    val endIndex = calculateLastIndexToInsert(textCopy, end, boldMark)
                    textCopy.insert(start, boldMark)
                    textCopy.insert(endIndex, boldMark)
                }
                is EmphasisSpan -> {
                    val endIndex = calculateLastIndexToInsert(textCopy, end, italicMark)
                    textCopy.insert(start, italicMark)
                    textCopy.insert(endIndex, italicMark)
                }
                is StrikethroughSpan -> {
                    val endIndex = calculateLastIndexToInsert(textCopy, end, strikeMark)
                    textCopy.insert(start, strikeMark)
                    textCopy.insert(endIndex, strikeMark)
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
        text.getSpans<MentionSpan>().forEach {
            val end = textCopy.getSpanEnd(it)
            val textToInsert = it.name + mentionMark
            textCopy.insert(end, textToInsert)
        }
        return textCopy.toString()
    }

    fun markwonBuilder(context: Context): Markwon = Markwon.builder(context)
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(MentionPlugin(context))
        .usePlugin(
            TaskListPlugin.create(
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.blue),
                Color.WHITE
            )
        ).build()

    fun markwonNotificationBuilder(context: Context): Markwon = Markwon.builder(context)
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                builder.setFactory(
                    Emphasis::class.java
                ) { _, _ -> StyleSpan(Typeface.ITALIC) }
            }
        })
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                builder.setFactory(
                    StrongEmphasis::class.java
                ) { _, _ -> StyleSpan(Typeface.BOLD) }
            }
        })
        .usePlugin(StrikethroughPlugin.create())
        .usePlugin(LinkifyPlugin.create())
        .usePlugin(
            TaskListPlugin.create(
                ContextCompat.getColor(context, R.color.blue),
                ContextCompat.getColor(context, R.color.blue),
                Color.WHITE
            )
        ).build()

    fun hasCurrentUserMention(text: String): Boolean {
        val session = MatrixSessionProvider.currentSession ?: return false
        val userName = session.getUserOrDefault(session.myUserId).notEmptyDisplayName()
        val mentionString = mentionMark + userName + mentionMark
        return text.contains(mentionString)
    }

    private fun calculateLastIndexToInsert(textCopy: Editable, spanEnd: Int, mark: String): Int {
        var endIndex = spanEnd + mark.length
        val lastChar = textCopy.getOrNull(spanEnd - 1).toString()
        if (lastChar == " " || lastChar == "\n") endIndex -= 1
        return endIndex
    }
}