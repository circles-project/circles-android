package org.futo.circles.gallery.feature.preview

import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.mapping.toPost
import org.futo.circles.model.PostContent
import org.futo.circles.model.PostContentType
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class MediaPreviewDataSource(
    private val roomId: String,
    private val eventId: String
) {

    private val session = MatrixSessionProvider.currentSession

    fun getPostContent(): PostContent? {
        val roomForMessage = session?.getRoom(roomId)
        val timelineEvent = roomForMessage?.getTimelineEvent(eventId) ?: return null
        val post = getPostContentTypeFor(timelineEvent)?.let { timelineEvent.toPost(it) }
            ?: return null
        return post.content
    }

    private fun getPostContentTypeFor(event: TimelineEvent): PostContentType? {
        val messageType = event.root.getClearContent()?.toModel<MessageContent>()?.msgType
        return PostContentType.values().firstOrNull { it.typeKey == messageType }
    }


}