package org.futo.circles.feature.timeline

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.model.CircleRoomTypeArg

class TimelineNavigator(private val fragment: TimelineDialogFragment) {

    fun navigateToCreatePost(
        roomId: String,
        eventId: String? = null,
        isEdit: Boolean = false
    ) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toCreatePostBottomSheet(roomId, eventId, isEdit)
        )
    }

    fun navigateToCreatePoll(roomId: String, eventId: String? = null) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toCreatePoll(roomId, eventId)
        )
    }

    fun navigateToInviteMembers(timelineId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toInviteMembersDialogFragment(timelineId)
        )
    }

    fun navigateToUpdateRoom(roomId: String, type: CircleRoomTypeArg) {
        val destination = if (type == CircleRoomTypeArg.Circle)
            TimelineDialogFragmentDirections.toUpdateCircleDialogFragment(roomId)
        else TimelineDialogFragmentDirections.toUpdateGroupDialogFragment(roomId)
        fragment.findNavController().navigate(destination)
    }

    fun navigateToManageMembers(timelineId: String, type: CircleRoomTypeArg) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toManageMembersDialogFragment(timelineId, type)
        )
    }

    fun navigateToFollowing(roomId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toFollowingDialogFragment(roomId)
        )
    }

    fun navigateToInfo(roomId: String, eventId: String) {
        fragment.findNavController()
            .navigate(TimelineDialogFragmentDirections.toPostInfo(roomId, eventId))
    }

    fun navigateToStateEvents(roomId: String) {
        fragment.findNavController()
            .navigate(TimelineDialogFragmentDirections.toStateEvents(roomId))
    }

    fun navigateToSaveToGallery(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toSaveToGalleyDialogFragment(roomId, eventId)
        )
    }

    fun navigateToReport(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toReportDialogFragment(roomId, eventId)
        )
    }

    fun navigateToShowMediaPreview(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toMediaPreviewDialogFragment(roomId, eventId)
        )
    }

    fun navigateToShowEmoji(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toEmojiBottomSheet(roomId, eventId)
        )
    }

    fun navigateToUserDialogFragment(userId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toUserDialogFragment(userId)
        )
    }

    fun navigateToThread(roomId: String, threadEventId: String) {
        fragment.findNavController().navigate(
            TimelineDialogFragmentDirections.toThreadTimeline(roomId, threadEventId)
        )
    }

    fun navigateToShareRoom(roomId: String) {
        fragment.findNavController()
            .navigate(TimelineDialogFragmentDirections.toShareRoom(roomId))
    }
}