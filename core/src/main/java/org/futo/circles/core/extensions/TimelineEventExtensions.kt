package org.futo.circles.core.extensions

import org.futo.circles.core.model.PostContentType
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent


fun TimelineEvent.getPostContentType(): PostContentType? {
    val messageType = if (root.getClearType() == EventType.MESSAGE) root.getClearContent()
        .toModel<MessageContent>()?.msgType
    else getLastMessageContent()?.msgType
    return PostContentType.entries.firstOrNull { it.typeKey == messageType }
}