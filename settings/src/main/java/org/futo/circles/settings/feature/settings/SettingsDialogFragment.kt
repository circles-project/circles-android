package org.futo.circles.settings.feature.settings

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.feature.uia.flow.reauth.ReAuthCancellationListener
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.openCustomTabUrl
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.whats_new.WhatsNewDialog
import org.futo.circles.core.model.DeactivateAccount
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.settings.SessionHolderActivity
import org.futo.circles.settings.databinding.DialogFragmentSettingsBinding
import org.futo.circles.settings.model.LogOut
import org.futo.circles.settings.model.SwitchUser

@AndroidEntryPoint
class SettingsDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentSettingsBinding>(DialogFragmentSettingsBinding::inflate),
    ReAuthCancellationListener {

    private val viewModel by viewModels<SettingsViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private val navigator by lazy { SettingsNavigator(this) }
    private val whatsNewDialog by lazy { WhatsNewDialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            vLogout.setOnClickListener {
                if (showNoInternetConnection()) return@setOnClickListener
                withConfirmation(LogOut()) {
                    loadingDialog.handleLoading(ResLoadingData(org.futo.circles.auth.R.string.log_out))
                    viewModel.logOut()
                }
            }
            vSwitchAccount.setOnClickListener { withConfirmation(SwitchUser()) { (activity as? SessionHolderActivity)?.stopSyncAndRestart() } }
            vChangePassphrase.setOnClickListener {
                if (showNoInternetConnection()) return@setOnClickListener
                viewModel.handleChangePasswordFlow()
            }
            vDeactivate.setOnClickListener {
                if (showNoInternetConnection()) return@setOnClickListener
                withConfirmation(DeactivateAccount()) {
                    loadingDialog.handleLoading(ResLoadingData())
                    viewModel.deactivateAccount()
                }
            }
            vLoginSessions.setOnClickListener { navigator.navigateToActiveSessions() }
            vPushNotifications.setOnClickListener { navigator.navigateToPushSettings() }
            vEditProfile.setOnClickListener { navigator.navigateToEditProfile() }
            vPrivacyPolicy.setOnClickListener { openCustomTabUrl(getString(org.futo.circles.core.R.string.privacy_policy_url)) }
            vAdvancedSettings.setOnClickListener { navigator.navigateToAdvancedSettings() }
            vAbout.setOnClickListener { navigator.navigateToCircleExplanation() }
            vChangeLog.setOnClickListener { if (!whatsNewDialog.isShowing) whatsNewDialog.show() }
            vIgnoredUsers.setOnClickListener { navigator.navigateToIgnoredUsers() }
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
        viewModel.changePasswordResponseLiveData.observeResponse(this,
            success = { showSuccess(getString(org.futo.circles.core.R.string.passphrase_changed)) },
            error = { message -> showError(message) },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
    }

    private fun clearSessionAndRestart() {
        (activity as? SessionHolderActivity)?.clearSessionAndRestart()
    }

    private fun setVersion() {
        binding.tvVersion.text = getString(
            org.futo.circles.core.R.string.version_format,
            CirclesAppConfig.appVersionName
        )
    }

    override fun onReAuthCanceled() {
        loadingDialog.dismiss()
    }

}