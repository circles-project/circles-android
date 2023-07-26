package org.futo.circles.feature.timeline

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.model.CircleRoomTypeArg

class TimelineNavigator(private val fragment: TimelineDialogFragment) {

    fun navigateToCreatePost(
        roomId: String,
        eventId: String? = null
    ) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toCreatePostBottomSheet(roomId, eventId, false)
        )
    }

    fun navigateToTimelineOptions(roomId: String, type: CircleRoomTypeArg) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toTimelineOptions(roomId, type)
        )
    }

    fun navigateToCreatePoll(roomId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toCreatePoll(roomId, null)
        )
    }

    fun navigateToShowMediaPreview(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toMediaPreviewDialogFragment(roomId, eventId)
        )
    }

    fun navigateToShowEmoji(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toEmojiBottomSheet(roomId, eventId)
        )
    }

    fun navigateToUserDialogFragment(userId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toUserDialogFragment(userId)
        )
    }

    fun navigateToThread(roomId: String, threadEventId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toThreadTimeline(roomId, threadEventId)
        )
    }

    fun navigatePostMenu(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toPostMenuBottomSheet(roomId, eventId)
        )
    }
}