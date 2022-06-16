package org.futo.circles.feature.settings.deactivate

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.DeactivateAccountDialogFragmentBinding
import org.futo.circles.extensions.findParentNavController
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showError
import org.futo.circles.feature.bottom_navigation.BottomNavigationFragmentDirections
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeactivateAccountDialogFragment :
    BaseFullscreenDialogFragment(DeactivateAccountDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<DeactivateAccountViewModel>()

    private val binding by lazy {
        getBinding() as DeactivateAccountDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            btnDelete.setOnClickListener {
                viewModel.deactivateAccount(tilPassword.getText())
                startLoading(btnDelete)
            }
            tilPassword.editText?.doAfterTextChanged {
                it?.let { btnDelete.isEnabled = tilPassword.getText().isNotEmpty() }
            }
        }
    }

    private fun setupObservers() {
        viewModel.deactivateLiveData.observeResponse(this,
            success = { navigateToLogin() },
            error = { showError(getString(R.string.invalid_auth)) }
        )
    }

    private fun navigateToLogin() {
        findParentNavController()?.navigate(BottomNavigationFragmentDirections.toLogInFragment())
    }
}