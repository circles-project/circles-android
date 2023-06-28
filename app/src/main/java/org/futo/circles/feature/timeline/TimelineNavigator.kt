package org.futo.circles.feature.timeline

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.model.CircleRoomTypeArg

class TimelineNavigator(private val fragment: TimelineDialogFragment) {

    fun navigateToCreatePost(
        roomId: String,
        eventId: String? = null,
        isEdit: Boolean = false
    ) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toCreatePostBottomSheet(roomId, eventId, isEdit)
        )
    }

    fun navigateToCreatePoll(roomId: String, eventId: String? = null) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toCreatePoll(roomId, eventId)
        )
    }

    fun navigateToInviteMembers(timelineId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toInviteMembersDialogFragment(timelineId)
        )
    }

    fun navigateToUpdateRoom(roomId: String, type: CircleRoomTypeArg) {
        val destination = if (type == CircleRoomTypeArg.Circle)
            TimelineDialogFragmentDirections.toUpdateCircleDialogFragment(roomId)
        else TimelineDialogFragmentDirections.toUpdateGroupDialogFragment(roomId)
        fragment.findNavController().navigateSafe(destination)
    }

    fun navigateToManageMembers(timelineId: String, type: CircleRoomTypeArg) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toManageMembersDialogFragment(timelineId, type)
        )
    }

    fun navigateToFollowing(roomId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toFollowingDialogFragment(roomId)
        )
    }

    fun navigateToInfo(roomId: String, eventId: String) {
        fragment.findNavController()
            .navigateSafe(TimelineDialogFragmentDirections.toPostInfo(roomId, eventId))
    }

    fun navigateToStateEvents(roomId: String) {
        fragment.findNavController()
            .navigateSafe(TimelineDialogFragmentDirections.toStateEvents(roomId))
    }

    fun navigateToSaveToGallery(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toSaveToGalleyDialogFragment(roomId, eventId)
        )
    }

    fun navigateToReport(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            TimelineDialogFragmentDirections.toReportDialogFragment(roomId, eventId)
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

    fun navigateToShareRoom(roomId: String) {
        fragment.findNavController()
            .navigateSafe(TimelineDialogFragmentDirections.toShareRoom(roomId, false))
    }
}