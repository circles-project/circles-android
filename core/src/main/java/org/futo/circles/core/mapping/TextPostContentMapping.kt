package org.futo.circles.core.mapping

import org.futo.circles.core.feature.markdown.MarkdownParser
import org.futo.circles.core.model.TextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent

fun TimelineEvent.toTextContent(): TextContent {
    val text = getTextEditableContent(false)
    val spanned = MarkdownParser.parse(text)
    return TextContent(text, spanned)
}



