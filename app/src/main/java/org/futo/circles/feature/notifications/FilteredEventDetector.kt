package org.futo.circles.feature.notifications

import com.google.gson.annotations.SerializedName
import org.futo.circles.feature.notifications.model.NotifiableEvent
import org.futo.circles.feature.notifications.model.NotifiableMessageEvent
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.Content
import org.matrix.android.sdk.api.session.events.model.isVoiceMessage
import org.matrix.android.sdk.api.session.events.model.toModel
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent
import org.matrix.android.sdk.api.session.room.model.message.MessageAudioContent
import org.matrix.android.sdk.api.session.room.model.message.MessageAudioEvent
import org.matrix.android.sdk.api.session.room.model.message.asMessageAudioEvent
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent


//TODO should be removed
class FilteredEventDetector {

    fun shouldBeIgnored(notifiableEvent: NotifiableEvent): Boolean {
        val session = MatrixSessionProvider.currentSession ?: return false

        if (notifiableEvent is NotifiableMessageEvent) {
            val room = session.getRoom(notifiableEvent.roomId) ?: return false
            val timelineEvent = room.getTimelineEvent(notifiableEvent.eventId) ?: return false
            return timelineEvent.shouldBeIgnored()
        }
        return false
    }

    private fun TimelineEvent.shouldBeIgnored(): Boolean {
        if (root.isVoiceMessage()) {
            val audioEvent = root.asMessageAudioEvent()
            return audioEvent.isVoiceBroadcast() && audioEvent?.sequence != 1
        }
        return false
    }
}

fun MessageAudioEvent?.isVoiceBroadcast() = this?.root?.getClearContent()?.get(VoiceBroadcastConstants.VOICE_BROADCAST_CHUNK_KEY) != null

fun MessageAudioEvent.getVoiceBroadcastChunk(): VoiceBroadcastChunk? {
    @Suppress("UNCHECKED_CAST")
    return (root.getClearContent()?.get(VoiceBroadcastConstants.VOICE_BROADCAST_CHUNK_KEY) as? Content).toModel<VoiceBroadcastChunk>()
}

val MessageAudioEvent.sequence: Int? get() = getVoiceBroadcastChunk()?.sequence

data class VoiceBroadcastChunk(
    @SerializedName("sequence") val sequence: Int? = null
)

object VoiceBroadcastConstants {
    const val STATE_ROOM_VOICE_BROADCAST_INFO = "io.element.voice_broadcast_info"
    const val VOICE_BROADCAST_CHUNK_KEY = "io.element.voice_broadcast_chunk"
    const val DEFAULT_CHUNK_LENGTH_IN_SECONDS = 120
    const val MAX_VOICE_BROADCAST_LENGTH_IN_SECONDS = 14_400 // 4 hours
}
