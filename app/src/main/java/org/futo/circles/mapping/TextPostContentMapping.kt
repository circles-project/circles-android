package org.futo.circles.mapping

import android.content.Context
import org.futo.circles.model.TextContent
import org.futo.circles.feature.timeline.post.markdown.MarkdownParser
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent

fun TimelineEvent.toTextContent(context: Context): TextContent = TextContent(
    message = MarkdownParser.markdownToEditable(getTextEditableContent(false), context)
)