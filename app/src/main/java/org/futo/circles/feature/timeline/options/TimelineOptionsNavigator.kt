package org.futo.circles.feature.timeline.options

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.model.CircleRoomTypeArg

class TimelineOptionsNavigator(private val fragment: TimelineOptionsDialogFragment) {


    fun navigateToInviteMembers(timelineId: String) {
        fragment.findNavController().navigateSafe(
            TimelineOptionsDialogFragmentDirections.toInviteMembersDialogFragment(timelineId)
        )
    }

    fun navigateToUpdateRoom(roomId: String, type: CircleRoomTypeArg) {
        val destination = if (type == CircleRoomTypeArg.Circle)
            TimelineOptionsDialogFragmentDirections.toUpdateCircleDialogFragment(roomId)
        else TimelineOptionsDialogFragmentDirections.toUpdateGroupDialogFragment(roomId)
        fragment.findNavController().navigateSafe(destination)
    }

    fun navigateToManageMembers(timelineId: String, type: CircleRoomTypeArg) {
        fragment.findNavController().navigateSafe(
            TimelineOptionsDialogFragmentDirections.toManageMembersDialogFragment(timelineId, type)
        )
    }

    fun navigateToFollowing(roomId: String) {
        fragment.findNavController().navigateSafe(
            TimelineOptionsDialogFragmentDirections.toFollowingDialogFragment(roomId)
        )
    }


    fun navigateToStateEvents(roomId: String) {
        fragment.findNavController()
            .navigateSafe(TimelineOptionsDialogFragmentDirections.toStateEvents(roomId))
    }


    fun navigateToShareRoom(roomId: String) {
        fragment.findNavController()
            .navigateSafe(TimelineOptionsDialogFragmentDirections.toShareRoom(roomId, false))
    }
}