package org.futo.circles.mapping

import org.futo.circles.model.TextContent
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageTextContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

fun TimelineEvent.toTextContent(): TextContent = TextContent(
    message = root.getClearContent().toModel<MessageTextContent>()?.body ?: ""
)