package org.futo.circles.feature.direct.timeline

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe

class DMTimelineNavigator(private val fragment: DMTimelineDialogFragment) {

    fun navigateToUserPage(userId: String) {
        fragment.findNavController().navigateSafe(
            DMTimelineDialogFragmentDirections.toUserNavGraph(userId)
        )
    }

    fun navigateToEmojiPicker(roomId: String? = null, eventId: String? = null) {
        fragment.findNavController().navigateSafe(
            DMTimelineDialogFragmentDirections.toEmojiBottomSheet(roomId, eventId)
        )
    }

    fun navigateToShowMediaPreview(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            DMTimelineDialogFragmentDirections.toMediaPreviewDialogFragment(roomId, eventId)
        )
    }

    fun navigateToDmMenu(roomId: String, eventId: String) {
        fragment.findNavController().navigateSafe(
            DMTimelineDialogFragmentDirections.toDmMenuBottomSheet(roomId, eventId)
        )
    }
}