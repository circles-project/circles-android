package org.futo.circles.feature.settings

import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import org.futo.circles.extensions.getSystemNoticesRoomId
import org.futo.circles.extensions.showError

class SettingsNavigator(private val fragment: SettingsFragment) {

    fun navigateToPushTest() {
        fragment.findNavController()
            .navigate(SettingsFragmentDirections.toNotificationTestFragment())
    }

    fun navigateToActiveSessions() {
        fragment.findNavController()
            .navigate(SettingsFragmentDirections.toActiveSessionsDialogFragment())
    }

    fun navigateToSystemNotices() {
        val systemNoticesRoomId = getSystemNoticesRoomId() ?: run {
            fragment.showError(fragment.getString(R.string.system_notices_room_not_found))
            return
        }
        fragment.findNavController().navigate(
            SettingsFragmentDirections.toSystemNoticesDialogFragment(systemNoticesRoomId)
        )
    }

    fun navigateToMatrixChangePassword() {
        fragment.findNavController()
            .navigate(SettingsFragmentDirections.toChangePasswordDialogFragment())
    }

    fun navigateToProfile() {
        fragment.findNavController()
            .navigate(SettingsFragmentDirections.toEditProfileDialogFragment())
    }

    fun navigateToReAuthStages() {
        fragment.findNavController()
            .navigate(SettingsFragmentDirections.toReAuthStagesDialogFragment())
    }

}