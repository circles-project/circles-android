package org.futo.circles.auth.feature.uia

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.DialogFragmentUiaBinding
import org.futo.circles.auth.feature.pass_phrase.recovery.EnterPassPhraseDialog
import org.futo.circles.auth.feature.pass_phrase.recovery.EnterPassPhraseDialogListener
import org.futo.circles.auth.feature.uia.flow.reauth.ReAuthCancellationListener
import org.futo.circles.auth.model.UIANavigationEvent
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.BackPressOwner
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.view.LoadingDialog

@AndroidEntryPoint
class UIADialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentUiaBinding::inflate), BackPressOwner {

    private val viewModel by viewModels<UIAViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentUiaBinding
    }

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }
    private var enterPassPhraseDialog: EnterPassPhraseDialog? = null

    private val deviceIntentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        enterPassPhraseDialog?.selectFile(uri)
    }

    private val childNavHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.apply {
            title = getString(
                when (UIADataSourceProvider.activeFlowType) {
                    UIAFlowType.Login -> R.string.log_in
                    UIAFlowType.Signup -> R.string.sign_up
                    UIAFlowType.ReAuth -> R.string.confirm_auth
                    UIAFlowType.ForgotPassword -> R.string.forgot_password
                    else -> R.string.log_in
                }
            )
            setNavigationOnClickListener { onBackPressed() }
        }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it) }
        viewModel.navigationLiveData.observeData(this) { event ->
            handleNavigation(event)
        }
        viewModel.subtitleLiveData.observeData(this) {
            binding.toolbar.subtitle = it
        }
        viewModel.restoreKeysLiveData.observeResponse(
            this,
            error = {
                showError(it)
                loadingDialog.dismiss()
            }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
        viewModel.finishUIAEventLiveData.observeData(this) {
            when (UIADataSourceProvider.activeFlowType) {
                UIAFlowType.Login -> viewModel.finishLogin(it)
                UIAFlowType.Signup -> viewModel.finishSignup(it)
                else -> dismiss()
            }
        }
        viewModel.createBackupResultLiveData.observeResponse(this,
            error = { message ->
                showError(message)
                loadingDialog.dismiss()
            }
        )
    }

    private fun handleNavigation(event: UIANavigationEvent) {
        val id = when (event) {
            UIANavigationEvent.TokenValidation -> R.id.to_validateToken
            UIANavigationEvent.Subscription -> R.id.to_subscriptions
            UIANavigationEvent.AcceptTerm -> R.id.to_acceptTerms
            UIANavigationEvent.ValidateEmail -> R.id.to_validateEmail
            UIANavigationEvent.Password -> R.id.to_password
            UIANavigationEvent.Username -> R.id.to_username
            UIANavigationEvent.Home -> R.id.to_homeFragment
            UIANavigationEvent.ConfigureWorkspace -> R.id.to_ConfigureWorkspace
            UIANavigationEvent.PassPhrase -> {
                showPassPhraseDialog()
                null
            }
        }
        id?.let { binding.navHostFragment.findNavController().navigateSafe(it) }
    }

    private fun showPassPhraseDialog() {
        enterPassPhraseDialog =
            EnterPassPhraseDialog(requireContext(), object : EnterPassPhraseDialogListener {
                override fun onRestoreBackupWithPassphrase(passphrase: String) {
                    viewModel.restoreBackupWithPassPhrase(passphrase)
                }

                override fun onRestoreBackupWithRawKey(key: String) {
                    viewModel.restoreBackupWithRawKey(key)
                }

                override fun onRestoreBackup(uri: Uri) {
                    viewModel.restoreBackup(uri)
                }

                override fun onDoNotRestore() {
                    viewModel.onDoNotRestoreBackup()
                }

                override fun onSelectFileClicked() {
                    deviceIntentLauncher.launch(recoveryKeyMimeType)
                }
            }).apply {
                setOnDismissListener { enterPassPhraseDialog = null }
                show()
            }
    }

    private fun showDiscardDialog() {
        showDialog(
            titleResIdRes = R.string.discard_current_auth_progress,
            negativeButtonVisible = true,
            positiveAction = {
                cancelReAuth()
                findNavController().popBackStack()
            }
        )
    }

    override fun onChildBackPress(callback: OnBackPressedCallback) {
        val includedFragmentsManager = childNavHostFragment.childFragmentManager
        if (includedFragmentsManager.backStackEntryCount == 1) {
            cancelReAuth()
            callback.remove()
            onBackPressed()
        } else {
            showDiscardDialog()
        }
    }

    private fun cancelReAuth() {
        parentFragment?.childFragmentManager?.fragments?.forEach {
            (it as? ReAuthCancellationListener)?.onReAuthCanceled()
        }
    }

    companion object {
        private const val recoveryKeyMimeType = "text/plain"
    }
}