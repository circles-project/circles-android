package org.futo.circles.core.timeline.post

import dagger.hilt.android.scopes.ViewModelScoped
import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.futo.circles.core.model.PostContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.model.message.MessageContent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import org.matrix.android.sdk.api.session.room.timeline.getLastMessageContent
import javax.inject.Inject


@ViewModelScoped
class PostContentDataSource @Inject constructor() {

    private val session = MatrixSessionProvider.currentSession

    fun getPost(roomId: String, eventId: String): Post? {
        val roomForMessage = session?.getRoom(roomId)
        val timelineEvent = roomForMessage?.getTimelineEvent(eventId) ?: return null
        return getPostContentTypeFor(timelineEvent)?.let { timelineEvent.toPost(it) }
    }

    fun getPostContent(roomId: String, eventId: String): PostContent? =
        getPost(roomId, eventId)?.content


    private fun getPostContentTypeFor(event: TimelineEvent): PostContentType? {
        val messageType = event.getLastMessageContent()?.msgType
        return PostContentType.values().firstOrNull { it.typeKey == messageType }
    }

}