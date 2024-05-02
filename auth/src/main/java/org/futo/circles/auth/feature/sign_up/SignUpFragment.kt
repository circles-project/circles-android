package org.futo.circles.auth.feature.sign_up

import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.radiobutton.MaterialRadioButton
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentSignUpBinding
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showError

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up),
    HasLoadingState {

    override val fragment: Fragment = this

    private val binding by viewBinding(FragmentSignUpBinding::bind)
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { onBackPressed() }
            serverDomainGroup.setOnCheckedChangeListener { _, _ ->
                setFlowsLoading(true)
                viewModel.loadSignupFlowsForDomain(getDomain())
            }
            CirclesAppConfig.serverDomains.forEach { domain ->
                serverDomainGroup.addView(
                    MaterialRadioButton(requireContext()).apply {
                        text = domain
                        textSize = 20f
                    }
                )
            }
            (serverDomainGroup.children.first() as? RadioButton)?.toggle()
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
            success = {
                val hasSubscriptionFlow = viewModel.hasSubscriptionFlow(it)
                val hasFreeFlow = viewModel.hasFreeFlow(it)
                with(binding) {
                    btnSubscription.setIsVisible(hasSubscriptionFlow)
                    btnFree.setIsVisible(hasFreeFlow)
                    tvOr.setIsVisible(hasFreeFlow && hasSubscriptionFlow)
                }
            },
            error = { message ->
                showError(message)
                binding.lButtonsContainer.gone()
            },
            onRequestInvoked = { setFlowsLoading(false) }
        )
    }

    private fun setFlowsLoading(isLoading: Boolean) {
        with(binding) {
            lButtonsContainer.setIsVisible(!isLoading)
            flowProgress.setIsVisible(isLoading)
        }
    }

    private fun getDomain() =
        binding.serverDomainGroup
            .findViewById<MaterialRadioButton>(binding.serverDomainGroup.checkedRadioButtonId).text.toString()

}