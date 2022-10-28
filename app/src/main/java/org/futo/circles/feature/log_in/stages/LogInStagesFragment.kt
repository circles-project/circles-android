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
import org.futo.circles.core.auth.LoginStageNavigationEvent
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
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.toolbar.title = getString(R.string.log_in)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loginStageNavigationLiveData.observeData(this) { event ->
            val id = when (event) {
                LoginStageNavigationEvent.DirectPassword -> R.id.to_direct_login
                LoginStageNavigationEvent.Password -> R.id.to_password
                LoginStageNavigationEvent.Terms -> R.id.to_acceptTerms
                LoginStageNavigationEvent.BSspekeLogin -> R.id.to_bsspeke
                else -> throw IllegalArgumentException(getString(R.string.not_supported_navigation_event))
            }
            binding.navHostFragment.findNavController().navigate(id)
        }
        viewModel.subtitleLiveData.observeData(this) {
            binding.toolbar.subtitle = it
        }
        viewModel.restoreKeysLiveData.observeResponse(
            this,
            error = {
                showError(it, true)
                loadingDialog.dismiss()
            }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
        viewModel.spacesTreeLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
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

    companion object {
        private const val recoveryKeyMimeType = "text/plain"
    }
}