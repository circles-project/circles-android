package org.futo.circles.core.mapping

import io.noties.markwon.Markwon
import org.futo.circles.core.model.TextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getTextEditableContent

fun TimelineEvent.toTextContent(markwon: Markwon): TextContent = TextContent(
    message = markwon.toMarkdown(getTextEditableContent(false))

)


