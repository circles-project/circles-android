package org.futo.circles.feature.settings

import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.model.ShareUrlTypeArg

class SettingsNavigator(private val fragment: SettingsFragment) {

    fun navigateToPushSettings() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toPushNotificationsSettingsDialogFragment())
    }

    fun navigateToActiveSessions() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toActiveSessionsDialogFragment())
    }

    fun navigateToMatrixChangePassword() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toChangePasswordDialogFragment())
    }

    fun navigateToReAuthStages() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toReAuthStagesDialogFragment())
    }


    fun navigateToSubscriptionInfo() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toManageSubscriptionDialogFragment())
    }

}