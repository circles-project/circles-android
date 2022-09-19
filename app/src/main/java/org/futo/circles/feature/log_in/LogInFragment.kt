package org.futo.circles.feature.log_in

import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.BuildConfig
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.FragmentLogInBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeResponse
import org.koin.androidx.viewmodel.ext.android.viewModel


class LogInFragment : Fragment(R.layout.fragment_log_in), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<LogInViewModel>()
    private val binding by viewBinding(FragmentLogInBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setOnClickActions()
        setupObservers()
    }

    private fun setupViews() {
        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            listOf(BuildConfig.US_SERVER_DOMAIN, BuildConfig.EU_SERVER_DOMAIN)
        )
        binding.tvDomain.apply {
            setAdapter(adapter)
            onFocusChangeListener = OnFocusChangeListener { _, hasFocus ->
                binding.tilDomain.hint = if (hasFocus) getString(R.string.domain)
                else BuildConfig.US_SERVER_DOMAIN
            }
        }
        binding.tilDomain.hint = BuildConfig.US_SERVER_DOMAIN
    }

    private fun setupObservers() {
        viewModel.loginResultLiveData.observeResponse(this,
            success = {
                findNavController().navigate(LogInFragmentDirections.toLoginStagesFragment())
            }
        )
    }

    private fun setOnClickActions() {
        with(binding) {
            btnSignUp.setOnClickListener {
                findNavController().navigate(LogInFragmentDirections.toSignUpFragment())
            }
            btnLogin.setOnClickListener {
                startLoading(btnLogin)
                viewModel.startLogInFlow(buildUserIdFromInputs())
            }
        }
    }

    private fun buildUserIdFromInputs(): String {
        val userName = binding.tilUserName.getText()
        val domain = binding.tvDomain.text.toString().takeIf { it.isNotEmpty() }
            ?: BuildConfig.US_SERVER_DOMAIN
        return "@$userName:$domain"
    }
}