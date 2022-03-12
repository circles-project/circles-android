package com.futo.circles.feature.sign_up

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.SignUpFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.feature.sign_up.data_source.NavigationEvents
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment(R.layout.sign_up_fragment) {

    private val viewModel by viewModel<SignUpViewModel>()
    private val binding by viewBinding(SignUpFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }

        setupObservers()
        setupBackPressCallback()
    }

    private fun setupObservers() {
        viewModel.subtitleLiveData.observeData(this) {
            binding.toolbar.subtitle = it
        }
        viewModel.navigationLiveData.observeData(this) {
            handleNavigation(it)
        }
    }

    private fun setupBackPressCallback() {
        activity?.onBackPressedDispatcher
            ?.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val canPop = binding.navHostFragment.findNavController().popBackStack()
                    if (!canPop) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )
    }

    private fun handleNavigation(event: NavigationEvents) {
        val childNavigationController = binding.navHostFragment.findNavController()

        when (event) {
            NavigationEvents.TokenValidation -> childNavigationController.navigate(R.id.to_validateToken)
            NavigationEvents.AcceptTerm -> childNavigationController.navigate(R.id.to_acceptTerms)
            NavigationEvents.VerifyEmail -> childNavigationController.navigate(R.id.to_createAccount)
            NavigationEvents.SetupAvatar -> TODO()
            NavigationEvents.SetupCircles -> TODO()
            NavigationEvents.FinishSignUp -> findNavController()
                .navigate(SignUpFragmentDirections.toBottomNavigationFragment())
        }
    }

}