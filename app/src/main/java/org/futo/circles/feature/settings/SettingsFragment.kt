package org.futo.circles.feature.settings

import android.Manifest
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.BuildConfig
import org.futo.circles.MainActivity
import org.futo.circles.R
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.core.picker.RuntimePermissionHelper
import org.futo.circles.databinding.FragmentSettingsBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.home.SystemNoticesCountSharedViewModel
import org.futo.circles.feature.settings.active_sessions.verify.qr.QrScannerActivity
import org.futo.circles.mapping.notEmptyDisplayName
import org.futo.circles.model.ConfirmationType
import org.futo.circles.provider.PreferencesProvider
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.matrix.android.sdk.api.session.user.model.User

class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)
    private val viewModel by viewModel<SettingsViewModel>()
    private val systemNoticesCountViewModel by activityViewModel<SystemNoticesCountSharedViewModel>()
    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private val preferencesProvider by lazy { PreferencesProvider(requireContext()) }
    private val navigator by lazy { SettingsNavigator(this) }
    private val cameraPermissionHelper = RuntimePermissionHelper(this, Manifest.permission.CAMERA)
    private val scanActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { activityResult ->
            if (activityResult.resultCode == Activity.RESULT_OK) {
                val scannedQrCode = QrScannerActivity.getResultText(activityResult.data)
                viewModel.onProfileQrScanned(scannedQrCode)
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        updatePushNotificationStatus()
    }

    private fun setupViews() {
        with(binding) {
            tvLogout.setOnClickListener { withConfirmation(ConfirmationType.LOG_OUT) { viewModel.logOut() } }
            tvSwitchUser.setOnClickListener { withConfirmation(ConfirmationType.SWITCH_USER) { clearSessionAndRestart() } }
            ivProfile.setOnClickListener { navigator.navigateToProfile() }
            tvChangePassword.setOnClickListener { viewModel.handleChangePasswordFlow() }
            tvDeactivate.setOnClickListener { withConfirmation(ConfirmationType.DEACTIVATE_ACCOUNT) { viewModel.deactivateAccount() } }
            tvLoginSessions.setOnClickListener { navigator.navigateToActiveSessions() }
            lSystemNotices.setOnClickListener { navigator.navigateToSystemNotices() }
            tvClearCache.setOnClickListener { viewModel.clearCash() }
            lPushNotifications.setOnClickListener { openNotificationSettings() }
            tvVersion.setOnLongClickListener { toggleDeveloperMode(); true }
            tvNotificationsTest.setOnClickListener { navigator.navigateToPushTest() }
            ivScanProfile.setOnClickListener {
                cameraPermissionHelper.runWithPermission {
                    QrScannerActivity.startForResult(requireActivity(), scanActivityResultLauncher)
                }
            }
            ivShareProfile.setOnClickListener { navigator.navigateToShareProfile() }
        }
        setVersion()
        updateSettingsItemsVisibility()
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

    private fun bindProfile(user: User) {
        with(binding) {
            ivProfile.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
            tvUserName.text = user.notEmptyDisplayName()
            tvUserId.text = user.userId
        }
    }

    private fun updatePushNotificationStatus() {
        val isPushNotificationsAllowed =
            NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()
        binding.tvPushNotificationsStatus.text =
            getString(if (isPushNotificationsAllowed) R.string.enabled else R.string.disabled)
    }

    private fun clearSessionAndRestart() {
        (activity as? MainActivity)?.clearSessionAndRestart()
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
        updateSettingsItemsVisibility()
    }

    private fun updateSettingsItemsVisibility() {
        binding.tvNotificationsTest.setIsVisible(preferencesProvider.isDeveloperModeEnabled())
    }
}