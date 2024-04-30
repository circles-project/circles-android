package org.futo.circles.settings.feature.settings

import android.os.Bundle
import android.text.format.Formatter
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.feature.uia.flow.reauth.ReAuthCancellationListener
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.openCustomTabUrl
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.model.DeactivateAccount
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.provider.PreferencesProvider
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.settings.R
import org.futo.circles.settings.SessionHolderActivity
import org.futo.circles.settings.databinding.FragmentSettingsBinding
import org.futo.circles.settings.model.LogOut
import org.futo.circles.settings.model.SwitchUser
import org.matrix.android.sdk.internal.session.media.MediaUsageInfo

@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings), ReAuthCancellationListener {

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
            tvUserId.text = MatrixSessionProvider.currentSession?.myUserId
            tvManageSubscription.apply {
                setIsVisible(CirclesAppConfig.isGplayFlavor())
                setOnClickListener { navigator.navigateToSubscriptionInfo() }
            }
            tvLogout.setOnClickListener {
                withConfirmation(LogOut()) {
                    loadingDialog.handleLoading(LoadingData(org.futo.circles.auth.R.string.log_out))
                    viewModel.logOut()
                }
            }
            tvClearCache.setOnClickListener {
                (activity as? AppCompatActivity)?.let {
                    LauncherActivityUtils.clearCacheAndRestart(it)
                }
            }
            tvSwitchUser.setOnClickListener { withConfirmation(SwitchUser()) { (activity as? SessionHolderActivity)?.stopSyncAndRestart() } }
            tvChangePassword.setOnClickListener { viewModel.handleChangePasswordFlow() }
            tvAddEmail.setOnClickListener {
                loadingDialog.handleLoading(LoadingData())
                viewModel.handleChangeEmailFlow()
            }
            tvDeactivate.setOnClickListener {
                withConfirmation(DeactivateAccount()) {
                    loadingDialog.handleLoading(LoadingData())
                    viewModel.deactivateAccount()
                }
            }
            tvLoginSessions.setOnClickListener { navigator.navigateToActiveSessions() }
            tvVersion.setOnLongClickListener { toggleDeveloperMode(); true }
            tvPushNotifications.setOnClickListener { navigator.navigateToPushSettings() }
            tvEditProfile.setOnClickListener { navigator.navigateToEditProfile() }
            tvShareProfile.setOnClickListener { navigator.navigateToShareProfile(viewModel.getSharedCircleSpaceId()) }
            tvPrivacyPolicy.setOnClickListener { openCustomTabUrl(CirclesAppConfig.privacyPolicyUrl) }
        }
        setVersion()
    }

    private fun setupObservers() {
        viewModel.logOutLiveData.observeResponse(this,
            success = { clearSessionAndRestart() },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.deactivateLiveData.observeResponse(this,
            success = { clearSessionAndRestart() },
            error = { showError(getString(org.futo.circles.auth.R.string.the_password_you_entered_is_incorrect)) },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.startReAuthEventLiveData.observeData(this) {
            navigator.navigateToReAuthStages()
        }
        viewModel.navigateToMatrixChangePasswordEvent.observeData(this) {
            navigator.navigateToMatrixChangePassword()
        }
        viewModel.addEmailLiveData.observeResponse(this,
            success = { showSuccess(getString(org.futo.circles.core.R.string.email_added)) },
            error = { showError(getString(org.futo.circles.auth.R.string.the_password_you_entered_is_incorrect)) },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.changePasswordResponseLiveData.observeResponse(this,
            success = { showSuccess(getString(org.futo.circles.core.R.string.password_changed)) },
            error = { message -> showError(message) },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
        viewModel.mediaUsageInfoLiveData.observeResponse(this,
            error = { bindMediaUsageProgress(null) },
            success = { bindMediaUsageProgress(it) })
    }

    private fun bindMediaUsageProgress(mediaUsage: MediaUsageInfo?) {
        mediaUsage?.let {
            binding.mediaStorageProgress.apply {
                max = mediaUsage.storageSize.toInt()
                progress = mediaUsage.usedSize.toInt()
            }
            binding.tvMediaStorageInfo.text = getString(
                R.string.media_usage_format,
                Formatter.formatFileSize(requireContext(), mediaUsage.usedSize),
                Formatter.formatFileSize(requireContext(), mediaUsage.storageSize),
            )
        } ?: run {
            binding.tvMediaStorageInfo.text = getString(R.string.no_info_available)
        }
    }

    private fun clearSessionAndRestart() {
        (activity as? SessionHolderActivity)?.clearSessionAndRestart()
    }

    private fun setVersion() {
        binding.tvVersion.setText(
            getString(
                org.futo.circles.core.R.string.version_format,
                CirclesAppConfig.appVersionName
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

    override fun onReAuthCanceled() {
        loadingDialog.dismiss()
    }
}