package org.futo.circles.feature.timeline.post.menu

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe

class PostMenuBottomSheetNavigator(private val fragment: PostMenuBottomSheet) {

    fun navigateToEditPost(
        roomId: String, eventId: String
    ) {
        fragment.findNavController().navigateSafe(
            PostMenuBottomSheetDirections.toCreatePostBottomSheet(roomId, eventId, true)
        )
    }

    fun navigateToEditPoll(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            PostMenuBottomSheetDirections.toCreatePoll(roomId, eventId)
        )
    }

    fun navigateToInfo(roomId: String, eventId: String) {
        fragment.findNavController()
            .navigateSafe(PostMenuBottomSheetDirections.toPostInfo(roomId, eventId))
    }

    fun navigateToReport(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            PostMenuBottomSheetDirections.toReportDialogFragment(roomId, eventId)
        )
    }
}