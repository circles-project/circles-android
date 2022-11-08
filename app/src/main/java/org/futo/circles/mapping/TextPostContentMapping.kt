package org.futo.circles.mapping

import org.futo.circles.model.TextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import org.matrix.android.sdk.api.session.room.timeline.getTextDisplayableContent

fun TimelineEvent.toTextContent(): TextContent = TextContent(
    message = getLastMessageContent()?.getTextDisplayableContent() ?: ""
)