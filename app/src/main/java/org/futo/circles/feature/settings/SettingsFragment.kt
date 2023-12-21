package org.futo.circles.feature.settings

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.MainActivity
import org.futo.circles.R
import org.futo.circles.auth.model.LogOut
import org.futo.circles.auth.model.SwitchUser
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.model.DeactivateAccount
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.databinding.FragmentSettingsBinding
import org.matrix.android.sdk.api.session.user.model.User
import org.matrix.android.sdk.internal.session.media.MediaUsageInfo

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel by viewModels<SettingsViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }
    private val navigator by lazy { SettingsNavigator(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateMediaUsageInfo()
    }

    private fun setupViews() {
        with(binding) {
            tvManageSubscription.apply {
                setIsVisible(CirclesAppConfig.isGplayFlavor())
                setOnClickListener { navigator.navigateToSubscriptionInfo() }
            }
            tvLogout.setOnClickListener {
                withConfirmation(LogOut()) {
                    loadingDialog.handleLoading(LoadingData(R.string.log_out))
                    viewModel.logOut()
                }
            }
            tvSwitchUser.setOnClickListener { withConfirmation(SwitchUser()) { (activity as? MainActivity)?.stopSyncAndRestart() } }
            ivEditProfile.setOnClickListener { navigator.navigateToProfile() }
            tvChangePassword.setOnClickListener { viewModel.handleChangePasswordFlow() }
            tvDeactivate.setOnClickListener {
                withConfirmation(DeactivateAccount()) {
                    loadingDialog.handleLoading(LoadingData())
                    viewModel.deactivateAccount()
                }
            }
            tvLoginSessions.setOnClickListener { navigator.navigateToActiveSessions() }
            tvVersion.setOnLongClickListener { toggleDeveloperMode(); true }
            tvPushNotifications.setOnClickListener { navigator.navigateToPushSettings() }
            ivShareProfile.setOnClickListener { navigator.navigateToShareProfile(viewModel.getSharedCircleSpaceId()) }
            tvIgnoredUsers.setOnClickListener { navigator.navigateToIgnoredUsers() }
        }
        setVersion()
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it) }
        viewModel.logOutLiveData.observeResponse(this,
            success = { clearSessionAndRestart() },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.profileLiveData.observeData(this) {
            it.getOrNull()?.let { bindProfile(it) }
        }
        viewModel.deactivateLiveData.observeResponse(this,
            success = { clearSessionAndRestart() },
            error = { showError(getString(org.futo.circles.auth.R.string.invalid_auth)) },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.startReAuthEventLiveData.observeData(this) {
            navigator.navigateToReAuthStages()
        }
        viewModel.navigateToMatrixChangePasswordEvent.observeData(this) {
            navigator.navigateToMatrixChangePassword()
        }
        viewModel.changePasswordResponseLiveData.observeResponse(this,
            success = { showSuccess(getString(org.futo.circles.core.R.string.password_changed)) },
            error = { message -> showError(message) },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
        viewModel.mediaUsageInfoLiveData.observeData(this) { mediaUsage ->
            bindMediaUsageProgress(mediaUsage)
        }
    }

    private fun bindMediaUsageProgress(mediaUsage: MediaUsageInfo?) {
        binding.lMediaStorage.setIsVisible(mediaUsage != null)
        mediaUsage ?: return
        binding.mediaStorageProgress.apply {
            max = mediaUsage.storageSize.toInt()
            progress = mediaUsage.usedSize.toInt()
        }
        binding.tvMediaStorageInfo.text = getString(
            R.string.media_usage_format,
            Formatter.formatFileSize(requireContext(), mediaUsage.usedSize),
            Formatter.formatFileSize(requireContext(), mediaUsage.storageSize),
        )
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
        binding.tvVersion.setText(
            getString(
                org.futo.circles.core.R.string.version_format,
                CirclesAppConfig.appVersion
            )
        )
    }

    private fun toggleDeveloperMode() {
        val isEnabled = preferencesProvider.isDeveloperModeEnabled()
        preferencesProvider.setDeveloperMode(!isEnabled)
        val messageId = if (isEnabled) R.string.developer_mode_disabled
        else R.string.developer_mode_enabled
        Toast.makeText(requireContext(), getString(messageId), Toast.LENGTH_LONG).show()
    }
}