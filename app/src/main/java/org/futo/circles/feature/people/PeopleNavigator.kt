package org.futo.circles.feature.people

import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.model.ShareUrlTypeArg

class PeopleNavigator(private val fragment: PeopleFragment) {

    fun navigateToUserPage(userId: String) {
        fragment.findNavController().navigateSafe(PeopleFragmentDirections.toUserFragment(userId))
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