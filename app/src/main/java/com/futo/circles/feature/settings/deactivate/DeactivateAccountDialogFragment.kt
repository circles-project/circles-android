package com.futo.circles.feature.settings.deactivate

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.DeactivateAccountDialogFragmentBinding
import com.futo.circles.extensions.findParentNavController
import com.futo.circles.extensions.getText
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showError
import com.futo.circles.feature.bottom_navigation.BottomNavigationFragmentDirections
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