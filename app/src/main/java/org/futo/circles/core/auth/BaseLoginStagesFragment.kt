package org.futo.circles.core.auth

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.BackPressOwner
import org.futo.circles.databinding.FragmentLoginStagesBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.onBackPressed
import org.futo.circles.extensions.showDialog

abstract class BaseLoginStagesFragment : Fragment(R.layout.fragment_login_stages), BackPressOwner {

    abstract val viewModel: BaseLoginStagesViewModel
    abstract val isReAuth: Boolean
    abstract val title: String
    protected val binding by viewBinding(FragmentLoginStagesBinding::bind)

    private val childNavHostFragment by lazy {
        childFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { onBackPressed() }
        binding.toolbar.title = title
        setupObservers()
    }

    open fun setupObservers() {
        viewModel.loginStageNavigationLiveData.observeData(this) { event ->
            when (event) {
                LoginStageNavigationEvent.DirectPassword -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_direct_login)
                LoginStageNavigationEvent.Password -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_password)
                LoginStageNavigationEvent.Terms -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_acceptTerms)
                LoginStageNavigationEvent.BSspeke -> binding.navHostFragment.findNavController()
                    .navigate(R.id.to_bsspeke)
                else -> throw IllegalArgumentException(getString(R.string.not_supported_navigation_event))
            }
        }
        viewModel.subtitleLiveData.observeData(this) {
            binding.toolbar.subtitle = it
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