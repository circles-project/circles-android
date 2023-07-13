package org.futo.circles.settings.feature

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.utils.getSharedCirclesSpaceId
import org.futo.circles.core.utils.getSystemNoticesRoomId
import org.futo.circles.settings.R

class SettingsNavigator(private val fragment: SettingsFragment) {

    fun navigateToPushSettings() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toPushNotificationsSettingsDialogFragment())
    }

    fun navigateToActiveSessions() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toActiveSessionsDialogFragment())
    }

    fun navigateToSystemNotices() {
        val systemNoticesRoomId = getSystemNoticesRoomId() ?: run {
            fragment.showError(fragment.getString(R.string.system_notices_room_not_found))
            return
        }
        fragment.findNavController().navigateSafe(
            SettingsFragmentDirections.toSystemNoticesDialogFragment(systemNoticesRoomId)
        )
    }

    fun navigateToMatrixChangePassword() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toChangePasswordDialogFragment())
    }

    fun navigateToProfile() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toEditProfileDialogFragment())
    }

    fun navigateToReAuthStages() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toReAuthStagesDialogFragment())
    }

    fun navigateToShareProfile() {
        val sharedSpaceId = getSharedCirclesSpaceId() ?: kotlin.run {
            fragment.showError(
                fragment.requireContext().getString(R.string.shared_circles_space_not_found)
            )
            return
        }
        fragment.findNavController()
            .navigateSafe(
                SettingsFragmentDirections.toShareProfileDialogFragment(sharedSpaceId, true)
            )
    }

}