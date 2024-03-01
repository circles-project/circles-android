package org.futo.circles.auth.feature.uia

import android.app.Dialog
import android.content.DialogInterface
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
import org.futo.circles.auth.model.AuthUIAScreenNavigationEvent
import org.futo.circles.auth.model.UIAFlowType
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

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        object : Dialog(requireContext(), theme) {
            @Suppress("OVERRIDE_DEPRECATION")
            override fun onBackPressed() {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        UIADataSourceProvider.clear()
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
            setNavigationOnClickListener { handleBackAction() }
        }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it) }
        viewModel.stagesNavigationLiveData.observeData(this) { event ->
            handleStagesNavigation(event)
        }
        viewModel.navigationLiveData.observeData(this) { event ->
            handleScreenNavigation(event)
        }
        viewModel.subtitleLiveData.observeData(this) { (number, size) ->
            binding.toolbar.subtitle =
                getString(R.string.sign_up_stage_subtitle_format, number, size)
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
                UIAFlowType.ForgotPassword -> viewModel.finishForgotPassword(it)
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

    private fun handleScreenNavigation(event: AuthUIAScreenNavigationEvent) {
        when (event) {
            AuthUIAScreenNavigationEvent.Home -> findNavController().navigateSafe(
                UIADialogFragmentDirections.toHomeFragment()
            )

            AuthUIAScreenNavigationEvent.ConfigureWorkspace -> findNavController().navigateSafe(
                UIADialogFragmentDirections.toConfigureWorkspace()
            )

            AuthUIAScreenNavigationEvent.PassPhrase -> showPassPhraseDialog()
        }
    }

    private fun handleStagesNavigation(event: UIANavigationEvent) {
        val id = when (event) {
            UIANavigationEvent.TokenValidation -> R.id.to_validateToken
            UIANavigationEvent.Subscription -> R.id.to_subscriptions
            UIANavigationEvent.AcceptTerm -> R.id.to_acceptTerms
            UIANavigationEvent.ValidateEmail -> R.id.to_validateEmail
            UIANavigationEvent.Password -> R.id.to_password
            UIANavigationEvent.Username -> R.id.to_username
        }
        binding.navHostFragment.findNavController().navigateSafe(id)
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
                    viewModel.cancelRestore()
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
        handleBackAction(callback)
    }

    private fun handleBackAction(callback: OnBackPressedCallback? = null) {
        val includedFragmentsManager = childNavHostFragment.childFragmentManager
        if (includedFragmentsManager.backStackEntryCount == 1) {
            cancelReAuth()
            callback?.remove()
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