package com.futo.circles.feature.log_in

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.databinding.LogInFragmentBinding
import com.futo.circles.extensions.observeResponse
import org.koin.androidx.viewmodel.ext.android.viewModel


class LogInFragment : Fragment(R.layout.log_in_fragment), HasLoadingState {

    override val fragment: Fragment = this
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
            success = { navigateToBottomMenuFragment() }
        )
        viewModel.signUpEventResultLiveData.observeResponse(
            this,
            success = { navigateToSignUp() },
        )
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