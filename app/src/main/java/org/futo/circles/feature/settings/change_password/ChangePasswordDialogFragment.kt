package org.futo.circles.feature.settings.change_password

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.matrix.pass_phrase.LoadingDialog
import org.futo.circles.databinding.DialogFragmentChangePasswordBinding
import org.futo.circles.extensions.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChangePasswordDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentChangePasswordBinding::inflate), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<ChangePasswordViewModel>()
    private val createPassPhraseLoadingDialog by lazy { LoadingDialog(requireContext()) }

    private val binding by lazy {
        getBinding() as DialogFragmentChangePasswordBinding
    }

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
                showSuccess(getString(R.string.password_changed), true)
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