package org.futo.circles.feature.reauth

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import org.futo.circles.R
import org.futo.circles.core.auth.LoginStageNavigationEvent
import org.futo.circles.core.fragment.BackPressOwner
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.FragmentLoginStagesBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.extensions.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class ReAuthStagesDialogFragment :
    BaseFullscreenDialogFragment(FragmentLoginStagesBinding::inflate), BackPressOwner {

    private val viewModel by viewModel<ReAuthStageViewModel>()

    private val binding by lazy {
        getBinding() as FragmentLoginStagesBinding
    }

    private val childNavHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            @Deprecated(
                "Deprecated in Java",
                ReplaceWith("activity?.onBackPressedDispatcher?.onBackPressed()")
            )
            override fun onBackPressed() {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.toolbar.title = getString(R.string.confirm_auth)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loginStageNavigationLiveData.observeData(this) { event ->
            val id = when (event) {
                LoginStageNavigationEvent.DirectPassword -> R.id.to_direct_login
                LoginStageNavigationEvent.Password -> R.id.to_reAuthPassword
                LoginStageNavigationEvent.Terms -> R.id.to_ReAuthAcceptTerms
                LoginStageNavigationEvent.BSspeke -> R.id.to_reAuthBsSpeke
                else -> throw IllegalArgumentException(getString(R.string.not_supported_navigation_event))
            }
            binding.navHostFragment.findNavController().navigate(id)
        }
        viewModel.subtitleLiveData.observeData(this) {
            binding.toolbar.subtitle = it
        }
        viewModel.finishReAuthEventLiveData.observeData(this) {
            dismiss()
        }
    }

    private fun showDiscardDialog() {
        showDialog(
            titleResIdRes = R.string.discard_current_login_progress,
            negativeButtonVisible = true,
            positiveAction = { findNavController().popBackStack() })
    }

    override fun onChildBackPress(callback: OnBackPressedCallback) {
        val includedFragmentsManager = childNavHostFragment.childFragmentManager
        if (includedFragmentsManager.backStackEntryCount == 0) {
            callback.remove()
            onBackPressed()
        } else {
            showDiscardDialog()
        }
    }

}