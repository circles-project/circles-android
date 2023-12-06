package org.futo.circles.core.feature.markdown.mentions.plugin

import android.content.Context
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import org.commonmark.node.Text
import org.commonmark.parser.Parser
import org.futo.circles.core.feature.markdown.span.MentionSpan
import org.matrix.android.sdk.api.extensions.tryOrNull


class MentionPlugin(private val context: Context) : AbstractMarkwonPlugin() {

    private val processor = MentionDelimiterProcessor()

    override fun configureParser(builder: Parser.Builder) {
        builder.customDelimiterProcessor(processor)
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(
            MentionNode::class.java
        ) { visitor, simpleExtNode ->
            val start = visitor.length()
            visitor.visitChildren(simpleExtNode)
            val name = (simpleExtNode.firstChild as Text).literal
            val end = start + name.length
            visitor.builder().spannableStringBuilder().replace(start, end, "@")
            tryOrNull {
                SpannableBuilder.setSpans(
                    visitor.builder(),
                    MentionSpan(context, name),
                    start,
                    visitor.length()
                )
            }
        }
    }
}