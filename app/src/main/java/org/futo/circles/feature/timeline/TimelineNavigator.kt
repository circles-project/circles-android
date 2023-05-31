package org.futo.circles.feature.timeline

import androidx.navigation.fragment.findNavController

class TimelineNavigator(private val fragment: TimelineFragment) {

    fun navigateToCreatePost(
        roomId: String,
        eventId: String? = null,
        isEdit: Boolean = false
    ) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toCreatePostBottomSheet(roomId, eventId, isEdit)
        )
    }

    fun navigateToCreatePoll(roomId: String, eventId: String? = null) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toCreatePoll(roomId, eventId)
        )
    }

    fun navigateToInviteMembers(timelineId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toInviteMembersDialogFragment(timelineId)
        )
    }

    fun navigateToUpdateRoom(roomId: String, type: CircleRoomTypeArg) {
        val destination = if (type == CircleRoomTypeArg.Circle)
            TimelineFragmentDirections.toUpdateCircleDialogFragment(roomId)
        else TimelineFragmentDirections.toUpdateGroupDialogFragment(roomId)
        fragment.findNavController().navigate(destination)
    }

    fun navigateToManageMembers(timelineId: String, type: CircleRoomTypeArg) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toManageMembersDialogFragment(timelineId, type)
        )
    }

    fun navigateToFollowing(roomId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toFollowingDialogFragment(roomId)
        )
    }

    fun navigateToInfo(roomId: String, eventId: String) {
        fragment.findNavController()
            .navigate(TimelineFragmentDirections.toPostInfo(roomId, eventId))
    }

    fun navigateToStateEvents(roomId: String) {
        fragment.findNavController()
            .navigate(TimelineFragmentDirections.toStateEvents(roomId))
    }

    fun navigateToSaveToGallery(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toSaveToGalleyDialogFragment(roomId, eventId)
        )
    }

    fun navigateToReport(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toReportDialogFragment(roomId, eventId)
        )
    }

    fun navigateToShowMediaPreview(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toMediaPreviewDialogFragment(roomId, eventId)
        )
    }

    fun navigateToShowEmoji(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toEmojiBottomSheet(roomId, eventId)
        )
    }

    fun navigateToUserDialogFragment(userId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toUserDialogFragment(userId)
        )
    }

    fun navigateToThread(roomId: String, threadEventId: String) {
        fragment.findNavController().navigate(
            TimelineFragmentDirections.toThreadTimeline(roomId, threadEventId)
        )
    }
}