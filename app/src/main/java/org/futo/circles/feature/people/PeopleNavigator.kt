package org.futo.circles.feature.people

import androidx.navigation.fragment.findNavController
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.model.ShareUrlTypeArg
import org.futo.circles.model.PeopleCategoryTypeArg

class PeopleNavigator(private val fragment: PeopleFragment) {

    fun navigateToInvites() {
        fragment.findNavController().navigateSafe(PeopleFragmentDirections.toInvites())
    }

    fun navigateToUserPage(userId: String) {
        fragment.findNavController().navigateSafe(PeopleFragmentDirections.toUserFragment(userId))
    }

    fun navigateToPeopleCategoryPage(categoryType: PeopleCategoryTypeArg) {
        fragment.findNavController()
            .navigateSafe(PeopleFragmentDirections.toPeopleCategoryDialogFragment(categoryType))
    }

    fun navigateToEditProfile() {
        fragment.findNavController()
            .navigateSafe(PeopleFragmentDirections.toEditProfileDialogFragment())
    }

}