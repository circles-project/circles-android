package org.futo.circles.mapping

import org.futo.circles.model.TextContent
import org.futo.circles.view.markdown.MarkdownParser
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent

fun TimelineEvent.toTextContent(): TextContent = TextContent(
    message = MarkdownParser.markdownToEditable(getTextEditableContent(false))
)