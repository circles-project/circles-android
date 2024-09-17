package org.futo.circles.auth.feature.sign_up

import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentSignupBinding
import org.futo.circles.core.base.DEFAULT_DOMAIN
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed


@AndroidEntryPoint
class SignUpFragment : BaseBindingFragment<FragmentSignupBinding>(FragmentSignupBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<SignupViewModel>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.window?.statusBarColor =
            ContextCompat.getColor(requireContext(), org.futo.circles.core.R.color.grey_cool_200)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            ivBack.setOnClickListener { onBackPressed() }
            btnLogin.setOnClickListener {
                findNavController().navigateSafe(SignUpFragmentDirections.toLogInFragment())
            }
            btnSignUp.setOnClickListener {
                startLoading(binding.btnSignUp)
                viewModel.startSignUp(DEFAULT_DOMAIN)
            }
            lUsServer.setOnClickListener {
                cbUs.isChecked = true
                cbEu.isChecked = false
            }
            lEuServer.setOnClickListener {
                cbEu.isChecked = true
                cbUs.isChecked = false
            }
        }
    }

    private fun setupObservers() {
        viewModel.startSignUpEventLiveData.observeResponse(
            this,
            success = {
                findNavController().navigateSafe(SignUpFragmentDirections.toUiaFragment())
            }
        )
    }

}