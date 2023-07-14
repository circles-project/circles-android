package org.futo.circles.settings.feature

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.MainActivity
import org.futo.circles.auth.model.LogOut
import org.futo.circles.auth.model.SwitchUser
import org.futo.circles.core.CirclesAppConfig
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.settings.R
import org.futo.circles.settings.databinding.FragmentSettingsBinding
import org.futo.circles.settings.feature.system_notices.SystemNoticesCountSharedViewModel
import org.futo.circles.settings.model.DeactivateAccount
import org.futo.circles.settings.model.LogOut
import org.matrix.android.sdk.api.session.user.model.User

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel by viewModels<SettingsViewModel>()
    private val systemNoticesCountViewModel by activityViewModels<SystemNoticesCountSharedViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }
    private val navigator by lazy { SettingsNavigator(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tvLogout.setOnClickListener { withConfirmation(LogOut()) { viewModel.logOut() } }
            tvSwitchUser.setOnClickListener { withConfirmation(SwitchUser()) { (activity as? MainActivity)?.stopSyncAndRestart() } }
            ivProfile.setOnClickListener { navigator.navigateToProfile() }
            tvChangePassword.setOnClickListener { viewModel.handleChangePasswordFlow() }
            tvDeactivate.setOnClickListener { withConfirmation(DeactivateAccount()) { viewModel.deactivateAccount() } }
            tvLoginSessions.setOnClickListener { navigator.navigateToActiveSessions() }
            lSystemNotices.setOnClickListener { navigator.navigateToSystemNotices() }
            tvClearCache.setOnClickListener { viewModel.clearCash() }
            tvVersion.setOnLongClickListener { toggleDeveloperMode(); true }
            tvPushNotifications.setOnClickListener { navigator.navigateToPushSettings() }
            ivShareProfile.setOnClickListener { navigator.navigateToShareProfile() }
        }
        setVersion()
    }

    private fun setupObservers() {
        viewModel.logOutLiveData.observeResponse(this,
            success = { clearSessionAndRestart() }
        )
        viewModel.profileLiveData.observeData(this) {
            it.getOrNull()?.let { bindProfile(it) }
        }
        viewModel.loadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
        viewModel.deactivateLiveData.observeResponse(this,
            success = { clearSessionAndRestart() },
            error = { showError(getString(R.string.invalid_auth)) }
        )
        systemNoticesCountViewModel.systemNoticesCountLiveData?.observeData(this) {
            binding.ivNoticesCount.setCount(it ?: 0)
        }
        viewModel.startReAuthEventLiveData.observeData(this) {
            navigator.navigateToReAuthStages()
        }
        viewModel.navigateToMatrixChangePasswordEvent.observeData(this) {
            navigator.navigateToMatrixChangePassword()
        }
        viewModel.changePasswordResponseLiveData.observeResponse(this,
            success = { showSuccess(getString(org.futo.circles.core.R.string.password_changed)) },
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

    private fun bindProfile(user: User) {
        with(binding) {
            ivProfile.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
            tvUserName.text = user.notEmptyDisplayName()
            tvUserId.text = user.userId
        }
    }

    private fun clearSessionAndRestart() {
        (activity as? MainActivity)?.clearSessionAndRestart()
    }

    private fun setVersion() {
        binding.tvVersion.text = getString(R.string.version_format, CirclesAppConfig.appVersion)
    }

    private fun toggleDeveloperMode() {
        val isEnabled = preferencesProvider.isDeveloperModeEnabled()
        preferencesProvider.setDeveloperMode(!isEnabled)
        val messageId = if (isEnabled) R.string.developer_mode_disabled
        else R.string.developer_mode_enabled
        Toast.makeText(requireContext(), getString(messageId), Toast.LENGTH_LONG).show()
    }
}