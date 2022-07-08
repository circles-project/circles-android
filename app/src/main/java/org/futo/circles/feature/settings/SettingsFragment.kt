package org.futo.circles.feature.settings

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.amulyakhare.textdrawable.TextDrawable
import org.futo.circles.R
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.databinding.SettingsFragmentBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.bottom_navigation.BottomNavigationFragmentDirections
import org.futo.circles.feature.bottom_navigation.SystemNoticesCountSharedViewModel
import org.futo.circles.feature.settings.confirm_auth.ConfirmAuthDialog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.settings_fragment) {

    private val binding by viewBinding(SettingsFragmentBinding::bind)
    private val viewModel by viewModel<SettingsViewModel>()
    private val systemNoticesCountViewModel by sharedViewModel<SystemNoticesCountSharedViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private var confirmAuthDialog: ConfirmAuthDialog? = null

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
            tvDeactivate.setOnClickListener { showDeactivateAccountDialog() }
            tvLoginSessions.setOnClickListener { navigateToActiveSessions() }
            lSystemNotices.setOnClickListener { navigateToSystemNotices() }
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
        viewModel.deactivateLiveData.observeResponse(this,
            success = {
                confirmAuthDialog?.dismiss()
                navigateToLogin()
            },
            error = {
                confirmAuthDialog?.clearInput()
                showError(getString(R.string.invalid_auth))
            }
        )
        systemNoticesCountViewModel.systemNoticesCountLiveData?.observeData(this) {
            val count = it ?: 0
            handleSystemNoticesCount(count)
        }
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

    private fun navigateToActiveSessions() {
        findNavController().navigate(SettingsFragmentDirections.toActiveSessionsDialogFragment())
    }

    private fun navigateToSystemNotices() {
        val systemNoticesRoomId = getSystemNoticesRoomId() ?: return
        findNavController().navigate(
            SettingsFragmentDirections.toSystemNoticesDialogFragment(systemNoticesRoomId)
        )
    }

    private fun handleSystemNoticesCount(count: Int) {
        binding.ivNoticesCount.setIsVisible(count > 0)
        if (count > 0) {
            binding.ivNoticesCount.setImageDrawable(
                TextDrawable.Builder()
                    .setShape(TextDrawable.SHAPE_ROUND_RECT)
                    .setColor(
                        ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
                    )
                    .setTextColor(Color.WHITE)
                    .setBold()
                    .setText(count.toString())
                    .build()
            )
        }
    }

    private fun showLogoutDialog() {
        showDialog(
            titleResIdRes = R.string.log_out,
            messageResId = R.string.log_out_message,
            positiveButtonRes = R.string.log_out,
            negativeButtonVisible = true,
            positiveAction = { viewModel.logOut() })
    }

    private fun showDeactivateAccountDialog() {
        confirmAuthDialog = ConfirmAuthDialog(
            context = requireContext(),
            message = getString(R.string.deactivate_message),
            onConfirmed = { password -> viewModel.deactivateAccount(password) }
        ).apply {
            show()
            setOnDismissListener { confirmAuthDialog = null }
        }
    }

}