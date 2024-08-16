package org.futo.circles.settings.feature.settings

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe

class SettingsNavigator(private val fragment: SettingsDialogFragment) {

    fun navigateToPushSettings() {
        fragment.findNavController()
            .navigateSafe(SettingsDialogFragmentDirections.toPushNotificationsSettingsDialogFragment())
    }

    fun navigateToActiveSessions() {
        fragment.findNavController()
            .navigateSafe(SettingsDialogFragmentDirections.toActiveSessionsDialogFragment())
    }

    fun navigateToMatrixChangePassword() {
        fragment.findNavController()
            .navigateSafe(SettingsDialogFragmentDirections.toChangePasswordDialogFragment())
    }

    fun navigateToReAuthStages() {
        fragment.findNavController()
            .navigateSafe(SettingsDialogFragmentDirections.toUiaDialogFragment())
    }


    fun navigateToSubscriptionInfo() {
        fragment.findNavController()
            .navigateSafe(SettingsDialogFragmentDirections.toManageSubscriptionDialogFragment())
    }

    fun navigateToEditProfile() {
        fragment.findNavController()
            .navigateSafe(SettingsDialogFragmentDirections.toEditProfileDialogFragment())
    }

    fun navigateToAdvancedSettings() {
        fragment.findNavController()
            .navigateSafe(SettingsDialogFragmentDirections.toAdvancedSettingsDialogFragment())
    }
}