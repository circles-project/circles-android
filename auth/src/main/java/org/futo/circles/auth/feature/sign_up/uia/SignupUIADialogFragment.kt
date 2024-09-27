package org.futo.circles.auth.feature.sign_up.uia

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.DialogFragmentUiaBinding
import org.futo.circles.auth.feature.uia.UIADialogFragmentDirections
import org.futo.circles.auth.model.UIANavigationEvent
import org.futo.circles.core.base.fragment.BackPressOwner
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.view.LoadingDialog

@AndroidEntryPoint
class SignupUIADialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentUiaBinding>(DialogFragmentUiaBinding::inflate),
    BackPressOwner {

    private val viewModel by viewModels<SignupUIAViewModel>()

    private val loadingDialog by lazy { LoadingDialog(requireContext()) }

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
        dialog?.window?.let {
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.statusBarColor = ContextCompat.getColor(
                requireContext(),
                org.futo.circles.core.R.color.grey_cool_200
            )
        }
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

    private fun setupObservers() {
        viewModel.stagesNavigationLiveData.observeData(this) { event ->
            handleStagesNavigation(event)
        }
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            loadingDialog.handleLoading(it)
        }
        viewModel.finishUIAEventLiveData.observeData(this) {
            viewModel.finishSignup(it)
        }
        viewModel.createBackupResultLiveData.observeResponse(this,
            success = {
                findNavController().navigateSafe(UIADialogFragmentDirections.toCircleExplanation())
            },
            error = { message -> showError(message) },
            onRequestInvoked = { loadingDialog.dismiss() }
        )
    }

    private fun handleStagesNavigation(event: UIANavigationEvent) {
        val id = when (event) {
            UIANavigationEvent.AcceptTerm -> R.id.to_acceptTerms
            UIANavigationEvent.Recaptcha -> R.id.to_recaptcha
            else -> R.id.to_validateEmail
        }
        binding.navHostFragment.findNavController().navigateSafe(id)
    }


    private fun showDiscardDialog() {
        showDialog(
            titleResIdRes = R.string.discard_current_auth_progress,
            negativeButtonVisible = true,
            positiveAction = {
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
            callback?.remove()
            onBackPressed()
        } else {
            showDiscardDialog()
        }
    }

}