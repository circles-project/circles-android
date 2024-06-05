package org.futo.circles.auth.feature.sign_up

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentSignUpBinding
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showError

@AndroidEntryPoint
class SignUpFragment : BaseBindingFragment<FragmentSignUpBinding>(FragmentSignUpBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            btnSubscription.setOnClickListener {
                startLoading(btnSubscription)
                viewModel.startSignUp(true)
            }
            btnFree.setOnClickListener {
                startLoading(btnFree)
                viewModel.startSignUp(false)
            }
        }
    }

    private fun setupObservers() {
        viewModel.startSignUpEventLiveData.observeResponse(
            this,
            success = { findNavController().navigateSafe(SignUpFragmentDirections.toUiaFragment()) }
        )
        viewModel.signupFlowsLiveData.observeResponse(this,
            success = { (hasFree, hasSubscription) ->
                binding.btnSubscription.setIsVisible(hasSubscription)
                binding.btnFree.setIsVisible(hasFree)
            },
            error = { message ->
                showError(message)
            }
        )
        viewModel.flowsLoadingData.observeData(this) { isLoading ->
            binding.flowProgress.setIsVisible(isLoading)
            if (isLoading) {
                binding.btnSubscription.gone()
                binding.btnFree.gone()
            }
        }
    }

}