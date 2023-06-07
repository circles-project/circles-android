package org.futo.circles.gallery.feature.preview

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

class MediaPreviewDataSource @AssistedInject constructor(
    @Assisted private val roomId: String,
    @Assisted private val eventId: String
) {

    @AssistedFactory
    interface Factory {
        fun create(roomId: String, eventId: String): MediaPreviewDataSource
    }

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