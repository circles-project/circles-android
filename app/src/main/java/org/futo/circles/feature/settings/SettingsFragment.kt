package org.futo.circles.feature.settings

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.BuildConfig
import org.futo.circles.MainActivity
import org.futo.circles.R
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.databinding.FragmentSettingsBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.bottom_navigation.SystemNoticesCountSharedViewModel
import org.futo.circles.provider.PreferencesProvider
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel by viewModel<SettingsViewModel>()
    private val systemNoticesCountViewModel by activityViewModel<SystemNoticesCountSharedViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tvLogout.setOnClickListener { showLogoutDialog() }
            tvSwitchUser.setOnClickListener { showSwitchUserDialog() }
            tvEditProfile.setOnClickListener { navigateToProfile() }
            tvChangePassword.setOnClickListener { viewModel.handleChangePasswordFlow() }
            tvDeactivate.setOnClickListener { showDeactivateAccountDialog() }
            tvLoginSessions.setOnClickListener { navigateToActiveSessions() }
            lSystemNotices.setOnClickListener { navigateToSystemNotices() }
            tvClearCache.setOnClickListener { viewModel.clearCash() }
            tvVersion.setOnLongClickListener { toggleDeveloperMode(); true }
        }
        setVersion()
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
            success = { navigateToLogin() },
            error = { showError(getString(R.string.invalid_auth)) }
        )
        systemNoticesCountViewModel.systemNoticesCountLiveData?.observeData(this) {
            binding.ivNoticesCount.setCount(it ?: 0)
        }
        viewModel.startReAuthEventLiveData.observeData(this) {
            findNavController().navigate(SettingsFragmentDirections.toReAuthStagesDialogFragment())
        }
        viewModel.navigateToMatrixChangePasswordEvent.observeData(this) {
            navigateToMatrixChangePassword()
        }
        viewModel.changePasswordResponseLiveData.observeResponse(this,
            success = { showSuccess(getString(R.string.password_changed)) },
            error = { message ->
                showError(message)
                loadingDialog.dismiss()
            }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
        viewModel.clearCacheLiveData.observeData(this) {
            (activity as? MainActivity)?.restartForClearCache()
        }
    }

    private fun navigateToMatrixChangePassword() {
        findNavController().navigate(SettingsFragmentDirections.toChangePasswordDialogFragment())
    }

    private fun navigateToProfile() {
        findNavController().navigate(SettingsFragmentDirections.toEditProfileDialogFragment())
    }

    private fun navigateToLogin() {
        (activity as? MainActivity)?.restartForLogout()
    }

    private fun navigateToActiveSessions() {
        findNavController().navigate(SettingsFragmentDirections.toActiveSessionsDialogFragment())
    }

    private fun navigateToSystemNotices() {
        val systemNoticesRoomId = getSystemNoticesRoomId() ?: run {
            showError(getString(R.string.system_notices_room_not_found))
            return
        }
        findNavController().navigate(
            SettingsFragmentDirections.toSystemNoticesDialogFragment(systemNoticesRoomId)
        )
    }

    private fun showLogoutDialog() {
        showDialog(
            titleResIdRes = R.string.log_out,
            messageResId = R.string.log_out_message,
            positiveButtonRes = R.string.log_out,
            negativeButtonVisible = true,
            positiveAction = { viewModel.logOut() })
    }

    private fun showSwitchUserDialog() {
        showDialog(
            titleResIdRes = R.string.switch_user,
            messageResId = R.string.switch_user_message,
            positiveButtonRes = R.string.switch_str,
            negativeButtonVisible = true,
            positiveAction = { viewModel.switchUser() })
    }

    private fun showDeactivateAccountDialog() {
        showDialog(
            titleResIdRes = R.string.deactivate_my_account,
            messageResId = R.string.deactivate_message,
            positiveButtonRes = R.string.deactivate,
            negativeButtonVisible = true,
            positiveAction = { viewModel.deactivateAccount() })
    }

    private fun setVersion() {
        binding.tvVersion.text = getString(R.string.version_format, BuildConfig.VERSION_NAME)
    }

    private fun toggleDeveloperMode() {
        val isEnabled = preferencesProvider.isDeveloperModeEnabled()
        preferencesProvider.setDeveloperMode(!isEnabled)
        val messageId = if (isEnabled) R.string.developer_mode_disabled
        else R.string.developer_mode_enabled
        Toast.makeText(requireContext(), getString(messageId), Toast.LENGTH_LONG).show()
    }
}