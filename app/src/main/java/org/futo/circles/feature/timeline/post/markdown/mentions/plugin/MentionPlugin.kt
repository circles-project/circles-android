package org.futo.circles.feature.timeline.post.markdown.mentions.plugin

import android.content.Context
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.MarkwonVisitor
import io.noties.markwon.SpannableBuilder
import org.commonmark.parser.Parser
import org.futo.circles.feature.timeline.post.markdown.span.MentionSpan


class MentionPlugin(private val context: Context) : AbstractMarkwonPlugin() {

    private val processor = MentionDelimiterProcessor()

    override fun configureParser(builder: Parser.Builder) {
        builder.customDelimiterProcessor(processor)
    }

    override fun configureVisitor(builder: MarkwonVisitor.Builder) {
        builder.on(
            MentionNode::class.java
        ) { visitor, simpleExtNode ->
            val length = visitor.length()
            visitor.visitChildren(simpleExtNode)
            SpannableBuilder.setSpans(
                visitor.builder(),
                MentionSpan(context, visitor.builder().toString()),
                length,
                visitor.length()
            )
        }
    }
}