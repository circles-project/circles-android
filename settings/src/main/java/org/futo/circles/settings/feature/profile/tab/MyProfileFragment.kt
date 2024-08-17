package org.futo.circles.settings.feature.profile.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.observeData
import org.futo.circles.settings.R
import org.futo.circles.settings.databinding.FragmentMyProfileBinding
import org.futo.circles.settings.feature.profile.tab.list.ProfileTabUsersAdapter
import org.futo.circles.settings.model.PeopleCategoryType.Followers
import org.futo.circles.settings.model.PeopleCategoryType.Following
import org.futo.circles.settings.model.PeopleCategoryType.Other
import org.matrix.android.sdk.api.session.user.model.User

@AndroidEntryPoint
class MyProfileFragment :
    BaseBindingFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {

    private val viewModel by viewModels<MyProfileViewModel>()

    private val peopleAdapter by lazy {
        ProfileTabUsersAdapter(
            onUserClicked = { userId ->
                findNavController().navigateSafe(MyProfileFragmentDirections.toUserFragment(userId))
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            rvUsers.adapter = peopleAdapter
            btnSettings.setOnClickListener {
                findNavController().navigateSafe(MyProfileFragmentDirections.toSettingsDialogFragment())
            }
            btnEditProfile.setOnClickListener {
                findNavController().navigateSafe(MyProfileFragmentDirections.toEditProfileDialogFragment())
            }
            binding.vPeopleCategories.setOnCategorySelectListener {
                viewModel.selectPeopleCategory(it)
                binding.tvCategoryHeader.text = getString(
                    when (it) {
                        Followers -> R.string.my_followers
                        Following -> R.string.people_i_m_following
                        Other -> R.string.other_known_users
                    }
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.profileLiveData.observeData(this) { user ->
            user.getOrNull()?.let { bindProfile(it) }
        }
        viewModel.peopleLiveData.observeData(this) {
            peopleAdapter.submitList(it)
        }
        viewModel.peopleInfoLiveData.observeData(this) {
            binding.vPeopleCategories.setCountsPerCategory(it)
        }
    }

    private fun bindProfile(user: User) {
        with(binding) {
            ivProfile.loadUserProfileIcon(user.avatarUrl, user.userId)
            tvUserName.text = user.notEmptyDisplayName()
            tvUserId.text = user.userId
        }
    }
}