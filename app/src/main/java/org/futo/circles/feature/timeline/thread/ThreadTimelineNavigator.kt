package org.futo.circles.feature.timeline.thread

import androidx.navigation.fragment.findNavController

class ThreadTimelineNavigator(private val fragment: ThreadTimelineDialogFragment) {

    fun navigateToCreatePost(
        roomId: String,
        userName: String? = null,
        eventId: String? = null,
        isEdit: Boolean = false
    ) {
        fragment.findNavController().navigate(
            ThreadTimelineDialogFragmentDirections.toCreatePostBottomSheet(
                roomId, userName, eventId, isEdit
            )
        )
    }

    fun navigateToInfo(roomId: String, eventId: String) {
        fragment.findNavController()
            .navigate(ThreadTimelineDialogFragmentDirections.toPostInfo(roomId, eventId))
    }

    fun navigateToSaveToGallery(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            ThreadTimelineDialogFragmentDirections.toSaveToGalleyDialogFragment(roomId, eventId)
        )
    }

    fun navigateToReport(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            ThreadTimelineDialogFragmentDirections.toReportDialogFragment(roomId, eventId)
        )
    }

    fun navigateToShowMediaPreview(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            ThreadTimelineDialogFragmentDirections.toMediaPreviewDialogFragment(roomId, eventId)
        )
    }

    fun navigateToShowEmoji(roomId: String, eventId: String) {
        fragment.findNavController().navigate(
            ThreadTimelineDialogFragmentDirections.toEmojiBottomSheet(roomId, eventId)
        )
    }

    fun navigateToUserDialogFragment(userId: String) {
        fragment.findNavController().navigate(
            ThreadTimelineDialogFragmentDirections.toUserDialogFragment(userId)
        )
    }
}