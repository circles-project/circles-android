package org.futo.circles.auth.feature.sign_up.validate_email

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentValidateEmailBinding
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.isValidEmail
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.visible

@AndroidEntryPoint
class ValidateEmailFragment : ParentBackPressOwnerFragment(R.layout.fragment_validate_email),
    HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentValidateEmailBinding::bind)
    private val viewModel by viewModels<ValidateEmailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tilEmail.editText?.doAfterTextChanged {
                it?.let { btnSendCode.isEnabled = it.isValidEmail() }
            }
            tilValidationCode.editText?.doAfterTextChanged {
                it?.let { btnValidate.isEnabled = it.isNotEmpty() }
            }
            tilEmail.setEndIconOnClickListener {
                showDialog(
                    R.string.email,
                    R.string.email_usage_explanation
                )
            }
            tilValidationCode.setEndIconOnClickListener {
                showDialog(
                    R.string.validation_code,
                    R.string.validation_code_explanation
                )
            }
            btnSendCode.setOnClickListener {
                startLoading(btnSendCode)
                viewModel.sendCode(getEmailInput(), cbEmailUpdates.isChecked)
            }
            btnValidate.setOnClickListener {
                startLoading(btnValidate)
                viewModel.validateEmail(tilValidationCode.getText())
            }
        }
    }

    private fun setupObservers() {
        viewModel.sendCodeLiveData.observeResponse(this,
            success = { validationCodeSentState() })
        viewModel.validateEmailLiveData.observeResponse(this)
    }

    private fun getEmailInput(): String = binding.tilEmail.getText()

    private fun validationCodeSentState() {
        showSuccess(getString(R.string.validation_code_sent_to_format, getEmailInput()))
        binding.tilValidationCode.visible()
        binding.btnValidate.isEnabled =
            binding.tilValidationCode.editText?.text.isNullOrEmpty().not()
    }

}