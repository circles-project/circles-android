package com.futo.circles.feature.validate_email

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.databinding.ValidateEmailFragmentBinding
import com.futo.circles.extensions.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class ValidateEmailFragment : Fragment(R.layout.validate_email_fragment), HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(ValidateEmailFragmentBinding::bind)
    private val viewModel by viewModel<ValidateEmailViewModel>()

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
                viewModel.sendCode(getEmailInput())
            }
            btnValidate.setOnClickListener {
                startLoading(btnValidate)
                viewModel.validateEmail(tilValidationCode.editText?.text?.toString()?.trim() ?: "")
            }
        }
    }

    private fun setupObservers() {
        viewModel.sendCodeLiveData.observeResponse(this,
            success = { validationCodeSentState() })
        viewModel.validateEmailLiveData.observeResponse(this)
    }

    private fun getEmailInput(): String = binding.tilEmail.editText?.text?.toString()?.trim() ?: ""

    private fun validationCodeSentState() {
        showSuccess(getString(R.string.validation_code_sent_to_format, getEmailInput()))
        binding.tilValidationCode.visible()
        binding.btnValidate.isEnabled = binding.tilValidationCode.editText?.text.isNullOrEmpty().not()
    }

}