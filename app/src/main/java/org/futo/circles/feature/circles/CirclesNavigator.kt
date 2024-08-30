package org.futo.circles.feature.circles

import androidx.navigation.fragment.findNavController
import com.google.android.material.badge.ExperimentalBadgeUtils
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.model.TimelineTypeArg

@ExperimentalBadgeUtils
class CirclesNavigator(private val fragment: CirclesFragment) {

    fun navigateToAllPosts() {
        fragment.findNavController().navigateSafe(
            CirclesFragmentDirections.toTimeline(null, TimelineTypeArg.ALL_CIRCLES)
        )
    }

    fun navigateToRoomRequests() {
        fragment.findNavController().navigateSafe(CirclesFragmentDirections.toRoomRequests())
    }

    fun navigateToCircleTimeline(roomId: String) {
        fragment.findNavController().navigateSafe(
            CirclesFragmentDirections.toTimeline(roomId, TimelineTypeArg.CIRCLE)
        )
    }

    fun navigateToCreateCircle() {
        fragment.findNavController()
            .navigateSafe(CirclesFragmentDirections.toCreateCircleDialogFragment())
    }

}