package com.futo.circles.feature.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.matrix.pass_phrase.LoadingDialog
import com.futo.circles.databinding.SettingsFragmentBinding
import com.futo.circles.extensions.findParentNavController
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showDialog
import com.futo.circles.feature.bottom_navigation.BottomNavigationFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val binding by viewBinding(SettingsFragmentBinding::bind)
    private val viewModel by viewModel<SettingsViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tvLogout.setOnClickListener { showLogoutDialog() }
            tvEditProfile.setOnClickListener { navigateToProfile() }
            tvChangePassword.setOnClickListener { navigateToChangePassword() }
            tvDeactivate.setOnClickListener { navigateToDeactivateAccount() }
        }

    }

    private fun setupObservers() {
        viewModel.logOutLiveData.observeResponse(this,
            success = { navigateToLogin() }
        )
        viewModel.profileLiveData.observeData(this) {
            it.getOrNull()?.let { binding.vUser.setData(it) }
        }
        viewModel.loadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
    }

    private fun navigateToDeactivateAccount() {
        findNavController().navigate(SettingsFragmentDirections.toDeactivateAccountDialogFragment())
    }

    private fun navigateToChangePassword() {
        findNavController().navigate(SettingsFragmentDirections.toChangePasswordDialogFragment())
    }

    private fun navigateToProfile() {
        findNavController().navigate(SettingsFragmentDirections.toEditProfileDialogFragment())
    }

    private fun navigateToLogin() {
        findParentNavController()?.navigate(BottomNavigationFragmentDirections.toLogInFragment())
    }

    private fun showLogoutDialog() {
        showDialog(
            titleResIdRes = R.string.log_out,
            messageResId = R.string.log_out_message,
            positiveButtonRes = R.string.log_out,
            negativeButtonVisible = true,
            positiveAction = { viewModel.logOut() })
    }

}