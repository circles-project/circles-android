package com.futo.circles.feature.settings.change_password

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.ChangePasswordDialogFragmentBinding
import com.futo.circles.extensions.getText
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showSuccess
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordDialogFragment :
    BaseFullscreenDialogFragment(ChangePasswordDialogFragmentBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<ChangePasswordViewModel>()

    private val binding by lazy {
        getBinding() as ChangePasswordDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
            tilOldsPassword.editText?.doAfterTextChanged {
                it?.let { onPasswordsDataChanged() }
            }
            tilNewPassword.editText?.doAfterTextChanged {
                it?.let { onPasswordsDataChanged() }
            }
            tilRepeatPassword.editText?.doAfterTextChanged {
                it?.let { onPasswordsDataChanged() }
            }
            btnSave.setOnClickListener {
                viewModel.changePassword(tilOldsPassword.getText(), tilNewPassword.getText())
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.changePasswordResponseLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.password_changed), true)
                activity?.onBackPressed()
            }
        )
    }

    private fun onPasswordsDataChanged() {
        val old = binding.tilOldsPassword.getText()
        val new = binding.tilNewPassword.getText()
        val repeat = binding.tilRepeatPassword.getText()

        val isValid = old.isNotEmpty() && new.isNotEmpty() && repeat.isNotEmpty() && new == repeat
        binding.btnSave.isEnabled = isValid
    }

}