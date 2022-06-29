package org.futo.circles.feature.log_in

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.databinding.LogInFragmentBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showError
import org.koin.androidx.viewmodel.ext.android.viewModel


class LogInFragment : Fragment(R.layout.log_in_fragment), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<LogInViewModel>()
    private val binding by viewBinding(LogInFragmentBinding::bind)
    private val restorePassPhraseLoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickActions()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loginResultLiveData.observeResponse(this,
            success = { startLoading(binding.btnLogin) }
        )
        viewModel.restoreKeysLiveData.observeResponse(
            this,
            success = { startLoading(binding.btnLogin) },
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
        EnterPassPhraseDialog(requireContext(), object : EnterPassPhraseDialogListener {
            override fun onRestoreBackup(passphrase: String) {
                viewModel.restoreBackup(passphrase)
            }

            override fun onDoNotRestore() {
                viewModel.handleCirclesTree()
            }
        }).show()
    }

    private fun setOnClickActions() {
        with(binding) {
            btnSignUp.setOnClickListener { navigateToSignUp() }

            btnLogin.setOnClickListener {
                startLoading(btnLogin)
                viewModel.logIn(name = tilUserName.getText(), password = tilPassword.getText())
            }
        }
    }

    private fun navigateToSignUp() {
        findNavController().navigate(LogInFragmentDirections.toSignUpFragment())
    }

    private fun navigateToBottomMenuFragment() {
        findNavController().navigate(LogInFragmentDirections.toBottomNavigationFragment())
    }

    private fun navigateToSetupCircles() {
        findNavController().navigate(LogInFragmentDirections.toSetupCirclesFragment())
    }
}