package org.futo.circles.settings.feature.profile.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.settings.databinding.FragmentMyProfileBinding

@AndroidEntryPoint
class MyProfileFragment :
    BaseBindingFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {

    private val viewModel by viewModels<MyProfileViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnSettings.setOnClickListener {
                findNavController().navigateSafe(MyProfileFragmentDirections.toSettingsDialogFragment())
            }
            btnEditProfile.setOnClickListener {
                findNavController().navigateSafe(MyProfileFragmentDirections.toEditProfileDialogFragment())
            }
        }
    }

    private fun setupObservers() {

    }
}