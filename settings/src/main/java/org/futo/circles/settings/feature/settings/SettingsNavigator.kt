package org.futo.circles.settings.feature.settings

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.model.ShareUrlTypeArg
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

    fun navigateToMatrixChangePassword() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toChangePasswordDialogFragment())
    }

    fun navigateToReAuthStages() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toUiaDialogFragment())
    }


    fun navigateToSubscriptionInfo() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toManageSubscriptionDialogFragment())
    }

    fun navigateToEditProfile() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toEditProfileDialogFragment())
    }

    fun navigateToAdvancedSettings() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toAdvancedSettingsDialogFragment())
    }

    fun navigateToPhotos() {
        fragment.findNavController()
            .navigateSafe(SettingsFragmentDirections.toPhotosNavGraph())
    }

}