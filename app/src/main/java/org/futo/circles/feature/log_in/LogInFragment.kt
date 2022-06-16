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
        viewModel.loginResultLiveData.observeResponse(this)
        viewModel.restoreKeysLiveData.observeResponse(
            this,
            success = { navigateToBottomMenuFragment() },
            error = {
                showError(it, true)
                restorePassPhraseLoadingDialog.dismiss()
                navigateToBottomMenuFragment()
            }
        )
        viewModel.signUpEventResultLiveData.observeResponse(
            this,
            success = { navigateToSignUp() },
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            restorePassPhraseLoadingDialog.handleLoading(it)
        }
    }

    private fun setOnClickActions() {
        with(binding) {
            btnSignUp.setOnClickListener {
                startLoading(btnSignUp)
                viewModel.startSignUp()
            }

            btnLogin.setOnClickListener {
                startLoading(btnLogin)
                viewModel.logIn(
                    name = tilUserName.editText?.text.toString().trim(),
                    password = tilPassword.editText?.text.toString().trim()
                )
            }
        }
    }


    private fun navigateToSignUp() {
        findNavController().navigate(LogInFragmentDirections.toSignUpFragment())
    }

    private fun navigateToBottomMenuFragment() {
        findNavController().navigate(LogInFragmentDirections.toBottomNavigationFragment())
    }
}