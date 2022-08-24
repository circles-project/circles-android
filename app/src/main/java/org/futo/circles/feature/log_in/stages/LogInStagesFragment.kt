package org.futo.circles.feature.log_in.stages

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.databinding.FragmentLoginStagesBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showError
import org.futo.circles.feature.log_in.EnterPassPhraseDialog
import org.futo.circles.feature.log_in.EnterPassPhraseDialogListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogInStagesFragment : Fragment(R.layout.fragment_login_stages) {

    private val viewModel by viewModel<LoginStagesViewModel>()
    private val binding by viewBinding(FragmentLoginStagesBinding::bind)
    private val restorePassPhraseLoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                LoginNavigationEvent.Password -> TODO()
                LoginNavigationEvent.Terms -> TODO()
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
        EnterPassPhraseDialog(requireContext(), object : EnterPassPhraseDialogListener {
            override fun onRestoreBackup(passphrase: String) {
                viewModel.restoreBackup(passphrase)
            }

            override fun onDoNotRestore() {
                viewModel.onDoNotRestoreBackup()
            }
        }).show()
    }

    private fun navigateToBottomMenuFragment() {
        findNavController().navigate(LogInStagesFragmentDirections.toBottomNavigationFragment())
    }

    private fun navigateToSetupCircles() {
        findNavController().navigate(LogInStagesFragmentDirections.toSetupCirclesFragment())
    }
}