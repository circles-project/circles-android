package com.futo.circles.feature.sign_up

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.fragment.BackPressOwner
import com.futo.circles.databinding.SignUpFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.showDialog
import com.futo.circles.feature.sign_up.data_source.NavigationEvents
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpFragment : Fragment(R.layout.sign_up_fragment), BackPressOwner {

    private val viewModel by viewModel<SignUpViewModel>()
    private val binding by viewBinding(SignUpFragmentBinding::bind)

    private val childNavHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.subtitleLiveData.observeData(this) {
            binding.toolbar.subtitle = it
        }
        viewModel.navigationLiveData.observeData(this) {
            handleNavigation(it)
        }
    }


    private fun handleNavigation(event: NavigationEvents) {
        val childNavigationController = binding.navHostFragment.findNavController()

        when (event) {
            NavigationEvents.TokenValidation -> childNavigationController.navigate(R.id.to_validateToken)
            NavigationEvents.AcceptTerm -> childNavigationController.navigate(R.id.to_acceptTerms)
            NavigationEvents.ValidateEmail -> childNavigationController.navigate(R.id.to_validateEmail)
            NavigationEvents.SetupAvatar -> TODO()
            NavigationEvents.SetupCircles -> TODO()
            NavigationEvents.FinishSignUp -> findNavController()
                .navigate(SignUpFragmentDirections.toSetupProfileFragment())
        }
    }

    private fun showDiscardDialog() {
        showDialog(
            titleResIdRes = R.string.discard_current_registration_progress,
            negativeButtonVisible = true,
            positiveAction = {
                childNavHostFragment.navController.popBackStack(
                    R.id.selectSignUpTypeFragment, false
                )
            })
    }

    override fun onChildBackPress(callback: OnBackPressedCallback) {
        val includedFragmentsManager = childNavHostFragment.childFragmentManager
        if (includedFragmentsManager.backStackEntryCount == 0) {
            callback.remove()
            activity?.onBackPressed()
        } else {
            showDiscardDialog()
        }
    }

}