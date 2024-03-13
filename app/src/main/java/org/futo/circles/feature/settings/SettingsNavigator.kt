package org.futo.circles.feature.settings

import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.model.ShareUrlTypeArg
import org.futo.circles.feature.people.PeopleFragmentDirections

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
            .navigateSafe(PeopleFragmentDirections.toEditProfileDialogFragment())
    }

    fun navigateToShareProfile(sharedSpaceId: String?) {
        sharedSpaceId ?: kotlin.run {
            fragment.showError(
                fragment.requireContext().getString(R.string.shared_circles_space_not_found)
            )
            return
        }
        fragment.findNavController().navigateSafe(
            PeopleFragmentDirections.toShareProfileDialogFragment(
                sharedSpaceId,
                ShareUrlTypeArg.PROFILE
            )
        )
    }

}