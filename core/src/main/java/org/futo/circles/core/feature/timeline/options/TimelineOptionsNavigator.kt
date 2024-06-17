package org.futo.circles.core.feature.timeline.options

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.model.toShareUrlType

class TimelineOptionsNavigator(private val fragment: TimelineOptionsDialogFragment) {


    fun navigateToInviteMembers(timelineId: String) {
        fragment.findNavController().navigateSafe(
            TimelineOptionsDialogFragmentDirections.toInviteMembersDialogFragment(timelineId)
        )
    }

    fun navigateToUpdateRoom(roomId: String, type: CircleRoomTypeArg) {
        fragment.findNavController().navigateSafe(
            TimelineOptionsDialogFragmentDirections.toUpdateRoomDialogFragment(
                roomId,
                type
            )
        )
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


    fun navigateToShareRoom(roomId: String, type: CircleRoomTypeArg) {
        fragment.findNavController()
            .navigateSafe(
                TimelineOptionsDialogFragmentDirections.toShareRoom(
                    roomId, type.toShareUrlType()
                )
            )
    }

    fun navigateToRequestForInvite(roomTypeArg: CircleRoomTypeArg, timelineId: String) {
        fragment.findNavController()
            .navigateSafe(
                TimelineOptionsDialogFragmentDirections.toRoomRequests(roomTypeArg, timelineId)
            )
    }

    fun navigateFilterTimelines(circleId: String) {
        fragment.findNavController()
            .navigateSafe(
                TimelineOptionsDialogFragmentDirections.toFilterTimelinesDialogFragment(circleId)
            )
    }
}