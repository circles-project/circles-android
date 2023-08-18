package org.futo.circles.core.extensions

import org.futo.circles.core.model.PostContentType
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent


fun TimelineEvent.getPostContentType(): PostContentType? {
    val messageType = getLastMessageContent()?.msgType
    return PostContentType.values().firstOrNull { it.typeKey == messageType }
}