package com.futo.circles.feature.log_in

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.LogInFragmentBinding
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.setEnabledViews
import com.futo.circles.extensions.showError
import org.koin.androidx.viewmodel.ext.android.viewModel


class LogInFragment : Fragment(R.layout.log_in_fragment) {

    private val viewModel by viewModel<LogInViewModel>()
    private val binding by viewBinding(LogInFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickActions()
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loginResultLiveData.observeResponse(
            this,
            onRequestInvoked = { setLoadingState(false) },
            success = { navigateToBottomMenuFragment() },
            error = { showError(it) }
        )
        viewModel.signUpEventResultLiveData.observeResponse(
            this,
            onRequestInvoked = { setLoadingState(false) },
            success = { navigateToSignUp() },
            error = { showError(it) }
        )
    }

    private fun setOnClickActions() {
        with(binding) {
            btnSignUp.setOnClickWithLoading {
                setLoadingState(true)
                viewModel.startSignUp()
            }

            btnLogin.setOnClickWithLoading {
                setLoadingState(true)
                viewModel.logIn(
                    name = tilUserName.editText?.text.toString().trim(),
                    password = tilPassword.editText?.text.toString().trim()
                )
            }
        }
    }

    private fun setLoadingState(isLoading: Boolean) {
        setEnabledViews(!isLoading)
        if (!isLoading) {
            binding.btnLogin.setIsLoading(false)
            binding.btnSignUp.setIsLoading(false)
        }
    }

    private fun navigateToSignUp() {
        findNavController().navigate(LogInFragmentDirections.toSignUpFragment())
    }

    private fun navigateToBottomMenuFragment() {
        findNavController().navigate(LogInFragmentDirections.toBottomNavigationFragment())
    }

}