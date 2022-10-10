package org.futo.circles.feature.log_in.stages

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.BackPressOwner
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.databinding.FragmentLoginStagesBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.log_in.EnterPassPhraseDialog
import org.futo.circles.feature.log_in.EnterPassPhraseDialogListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogInStagesFragment : Fragment(R.layout.fragment_login_stages), BackPressOwner {

    private val viewModel by viewModel<LoginStagesViewModel>()
    private val binding by viewBinding(FragmentLoginStagesBinding::bind)
    private val restorePassPhraseLoadingDialog by lazy { LoadingDialog(requireContext()) }
    private var enterPassPhraseDialog: EnterPassPhraseDialog? = null

    private val childNavHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    private val deviceIntentLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri ?: return@registerForActivityResult
        enterPassPhraseDialog?.selectFile(uri)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        setupObservers()
    }

    private fun setupObservers() {
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
                LoginNavigationEvent.DirectPassword -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_direct_login)
                LoginNavigationEvent.Password -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_password)
                LoginNavigationEvent.Terms -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_acceptTerms)
                LoginNavigationEvent.BSspeke -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_bsspeke)
                else -> navigateToBottomMenuFragment()
            }
        }
        viewModel.messageEventLiveData.observeData(this) { messageId ->
            showError(requireContext().getString(messageId))
        }
        viewModel.subtitleLiveData.observeData(this) {
            binding.toolbar.subtitle = it
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

    private fun showDiscardDialog() {
        showDialog(
            titleResIdRes = R.string.discard_current_login_progress,
            negativeButtonVisible = true,
            positiveAction = { findNavController().popBackStack() })
    }

    override fun onChildBackPress(callback: OnBackPressedCallback) {
        val includedFragmentsManager = childNavHostFragment.childFragmentManager
        if (includedFragmentsManager.backStackEntryCount == 0) {
            callback.remove()
            onBackPressed()
        } else {
            showDiscardDialog()
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