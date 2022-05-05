package com.futo.circles.feature.circle_timeline

import android.annotation.SuppressLint
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.matrix.timeline.BaseTimelineFragment
import com.futo.circles.extensions.getCurrentUserPowerLevel
import com.futo.circles.extensions.getTimelineRoomFor
import com.futo.circles.extensions.setIsVisible
import com.futo.circles.extensions.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class CircleTimelineFragment : BaseTimelineFragment() {

    private val args: CircleTimelineFragmentArgs by navArgs()
    override val viewModel by viewModel<CircleTimelineViewModel> { parametersOf(args.roomId) }
    override val roomId by lazy { args.roomId }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.circle_timeline_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureCircle -> {
                navigateToUpdateCircle()
                return true
            }
            R.id.myFollowers -> {

                return true
            }
            R.id.iFollowing -> {

                return true
            }
            R.id.inviteFollowers -> {
                navigateToInviteMembers()
                return true
            }
            R.id.deleteCircle -> {
                showDeleteConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onUserAccessLevelChanged(powerLevelsContent: PowerLevelsContent) {
        val isUserAdmin = powerLevelsContent.getCurrentUserPowerLevel() == Role.Admin.value
        binding.fbCreatePost.setIsVisible(isUserAdmin)
    }

    override fun navigateToCreatePost(userName: String?, eventId: String?) {
        findNavController().navigate(
            CircleTimelineFragmentDirections.toCreatePostBottomSheet(userName, eventId)
        )
    }

    override fun navigateToEmojiPicker(eventId: String) {
        findNavController().navigate(CircleTimelineFragmentDirections.toEmojiBottomSheet(eventId))
    }

    override fun navigateToReport(roomId: String, eventId: String) {
        findNavController().navigate(
            CircleTimelineFragmentDirections.toReportDialogFragment(args.roomId, eventId)
        )
    }

    private fun showDeleteConfirmation() {
        showDialog(
            titleResIdRes = R.string.delete_circle,
            messageResId = R.string.delete_circle_message,
            positiveButtonRes = R.string.delete,
            negativeButtonVisible = true,
            positiveAction = { viewModel.deleteCircle() }
        )
    }

    private fun navigateToInviteMembers() {
        val timelineRoomId = getTimelineRoomFor(args.roomId)?.roomId ?: return
        findNavController().navigate(
            CircleTimelineFragmentDirections.toInviteMembersDialogFragment(timelineRoomId)
        )
    }

    private fun navigateToUpdateCircle() {
        findNavController().navigate(
            CircleTimelineFragmentDirections.toUpdateRoomDialogFragment(args.roomId)
        )
    }
}