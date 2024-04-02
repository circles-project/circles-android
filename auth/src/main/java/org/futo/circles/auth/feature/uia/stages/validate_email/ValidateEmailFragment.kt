package org.futo.circles.auth.feature.uia.stages.validate_email

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
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
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.extensions.showError
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
            tilEmail.apply {
                editText?.doAfterTextChanged {
                    it?.let { btnSendCode.isEnabled = it.isValidEmail() }
                }
                setEndIconOnClickListener {
                    showDialog(
                        R.string.email,
                        R.string.email_usage_explanation
                    )
                }
            }
            tilValidationCode.apply {
                editText?.doAfterTextChanged {
                    it?.let { btnValidate.isEnabled = it.isNotEmpty() }
                }
                setEndIconOnClickListener {
                    showDialog(
                        R.string.validation_code,
                        R.string.validation_code_explanation
                    )
                }
            }
            btnSendCode.setOnClickListener {
                startLoading(btnSendCode)
                viewModel.sendCode(
                    binding.tilEmail.getText(),
                    cbEmailUpdates.isChecked && cbEmailUpdates.isVisible
                )
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
        viewModel.validateEmailLiveData.observeResponse(this, error = {
            showError(getString(R.string.invalid_validation_code))
        })
        viewModel.showSubscribeCheckLiveData.observeData(this) {
            binding.cbEmailUpdates.setIsVisible(it)
        }
        viewModel.usersEmailLiveData.observeData(this) { emails ->
            val emailsText = emails.joinToString(separator = "\n")
            binding.tvValidateHint.apply {
                setIsVisible(emails.isNotEmpty())
                text = getString(R.string.please_use_one_of_your_verified_emails_format, emailsText)
            }
        }
    }

    private fun validationCodeSentState() {
        showSuccess(getString(R.string.validation_code_sent_to_format, binding.tilEmail.getText()))
        binding.tilValidationCode.visible()
        binding.btnValidate.isEnabled =
            binding.tilValidationCode.editText?.text.isNullOrEmpty().not()
    }

}