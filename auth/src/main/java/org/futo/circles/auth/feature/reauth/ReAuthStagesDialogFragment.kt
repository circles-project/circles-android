package org.futo.circles.auth.feature.reauth

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.base.LoginStageNavigationEvent
import org.futo.circles.auth.databinding.FragmentLoginStagesBinding
import org.futo.circles.core.base.fragment.BackPressOwner
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showDialog

@AndroidEntryPoint
class ReAuthStagesDialogFragment :
    BaseFullscreenDialogFragment(FragmentLoginStagesBinding::inflate), BackPressOwner {

    private val viewModel by viewModels<ReAuthStageViewModel>()

    private val binding by lazy {
        getBinding() as FragmentLoginStagesBinding
    }

    private val childNavHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        object : Dialog(requireContext(), theme) {
            @Suppress("OVERRIDE_DEPRECATION")
            override fun onBackPressed() {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.apply {
            title = getString(R.string.confirm_auth)
            setNavigationOnClickListener { handleBackAction() }
        }
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.loginStageNavigationLiveData.observeData(this) { event ->
            val id = when (event) {
                LoginStageNavigationEvent.DirectPassword -> R.id.to_direct_login
                LoginStageNavigationEvent.Password -> R.id.to_reAuthPassword
                LoginStageNavigationEvent.Terms -> R.id.to_ReAuthAcceptTerms
                LoginStageNavigationEvent.BSspekeLogin -> R.id.to_reAuthBsSpekeLogin
                LoginStageNavigationEvent.BSspekeSignup -> R.id.to_reAuthBsSpekeSignup
                else -> throw IllegalArgumentException(getString(R.string.not_supported_navigation_event))
            }
            binding.navHostFragment.findNavController().navigateSafe(id)
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
            positiveAction = {
                cancelReAuth()
                findNavController().popBackStack()
            }
        )
    }

    override fun onChildBackPress(callback: OnBackPressedCallback) {
        handleBackAction(callback)
    }

    private fun handleBackAction(callback: OnBackPressedCallback? = null) {
        val includedFragmentsManager = childNavHostFragment.childFragmentManager
        if (includedFragmentsManager.backStackEntryCount == 1) {
            cancelReAuth()
            callback?.remove()
            onBackPressed()
        } else {
            showDiscardDialog()
        }
    }

    private fun cancelReAuth() {
        parentFragment?.childFragmentManager?.fragments?.forEach {
            (it as? ReAuthCancellationListener)?.onReAuthCanceled()
        }

    }

}