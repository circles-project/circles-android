package org.futo.circles.feature.log_in.stages

import android.net.Uri
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import org.futo.circles.core.auth.BaseLoginStagesFragment
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showError
import org.futo.circles.feature.log_in.EnterPassPhraseDialog
import org.futo.circles.feature.log_in.EnterPassPhraseDialogListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogInStagesFragment : BaseLoginStagesFragment() {

    override val viewModel by viewModel<LoginStagesViewModel>()
    override val isReAuth: Boolean = false
    override val title: String = getString(R.string.log_in)

    private val restorePassPhraseLoadingDialog by lazy { LoadingDialog(requireContext()) }
    private var enterPassPhraseDialog: EnterPassPhraseDialog? = null

    private val deviceIntentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        enterPassPhraseDialog?.selectFile(uri)
    }

    override fun setupObservers() {
        super.setupObservers()
        viewModel.restoreKeysLiveData.observeResponse(
            this,
            error = {
                showError(it, true)
                restorePassPhraseLoadingDialog.dismiss()
            }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            restorePassPhraseLoadingDialog.handleLoading(it)
        }
        viewModel.loginNavigationLiveData.observeData(this) { event ->
            when (event) {
                LoginNavigationEvent.Main -> navigateToBottomMenuFragment()
                LoginNavigationEvent.SetupCircles -> navigateToSetupCircles()
                LoginNavigationEvent.PassPhrase -> showPassPhraseDialog()
                else -> navigateToBottomMenuFragment()
            }
        }
        viewModel.messageEventLiveData.observeData(this) { messageId ->
            showError(requireContext().getString(messageId))
        }
    }

    private fun showPassPhraseDialog() {
        enterPassPhraseDialog =
            EnterPassPhraseDialog(requireContext(), object : EnterPassPhraseDialogListener {
                override fun onRestoreBackup(passphrase: String) {
                    viewModel.restoreBackup(passphrase)
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

    private fun navigateToBottomMenuFragment() {
        findNavController().navigate(LogInStagesFragmentDirections.toBottomNavigationFragment())
    }

    private fun navigateToSetupCircles() {
        findNavController().navigate(LogInStagesFragmentDirections.toSetupCirclesFragment())
    }

    companion object {
        private const val recoveryKeyMimeType = "text/plain"
    }
}