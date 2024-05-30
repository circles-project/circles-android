package org.futo.circles.settings.feature.change_password

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.settings.databinding.DialogFragmentChangePasswordBinding

@AndroidEntryPoint
class ChangePasswordDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentChangePasswordBinding>(
        DialogFragmentChangePasswordBinding::inflate
    ), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<ChangePasswordViewModel>()
    private val createPassPhraseLoadingDialog by lazy { LoadingDialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tilOldsPassword.editText?.doAfterTextChanged {
                onPasswordsDataChanged()
            }
            tilNewPassword.editText?.doAfterTextChanged {
                binding.vPasswordStrength.calculateStrength(tilNewPassword.getText())
                onPasswordsDataChanged()
            }
            tilRepeatPassword.editText?.doAfterTextChanged {
                onPasswordsDataChanged()
            }
            btnSave.setOnClickListener {
                viewModel.changePassword(tilOldsPassword.getText(), tilNewPassword.getText())
                startLoading(btnSave)
            }
        }
    }

    private fun setupObservers() {
        viewModel.responseLiveData.observeResponse(this,
            success = {
                showSuccess(getString(org.futo.circles.core.R.string.password_changed))
                onBackPressed()
            },
            error = { message ->
                showError(message)
                createPassPhraseLoadingDialog.dismiss()
            }
        )
        viewModel.passPhraseLoadingLiveData.observeData(this) {
            createPassPhraseLoadingDialog.handleLoading(it)
        }
    }

    private fun onPasswordsDataChanged() {
        val old = binding.tilOldsPassword.getText()
        val new = binding.tilNewPassword.getText()
        val repeat = binding.tilRepeatPassword.getText()
        val isStrong = binding.vPasswordStrength.isPasswordStrong()

        val isValid =
            old.isNotEmpty() && new.isNotEmpty() && repeat.isNotEmpty() && new == repeat && isStrong
        binding.btnSave.isEnabled = isValid
    }

}