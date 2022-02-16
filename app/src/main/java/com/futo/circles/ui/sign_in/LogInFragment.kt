package com.futo.circles.ui.sign_in

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.LogInFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogInFragment : Fragment(R.layout.log_in_fragment) {

    companion object {
        fun newInstance() = LogInFragment()
    }

    private val viewModel by viewModel<LogInViewModel>()
    private val binding: LogInFragmentBinding by viewBinding()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSignUp.setOnClickListener { navigateToSignUp() }
    }

    private fun navigateToSignUp() {
        findNavController().navigate(LogInFragmentDirections.actionLogInFragmentToSignUpFragment())
    }

}