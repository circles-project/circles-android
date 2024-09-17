package org.futo.circles.auth.feature.uia.stages.validate_email

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.otpview.OTPListener
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentValidateEmailBinding
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.isValidEmail
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.openCustomTabUrl
import org.futo.circles.core.extensions.showError
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.visible

@AndroidEntryPoint
class ValidateEmailFragment :
    ParentBackPressOwnerFragment<FragmentValidateEmailBinding>(FragmentValidateEmailBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModels<ValidateEmailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            ivBack.setOnClickListener { onBackPressed() }
            tilEmail.editText?.doAfterTextChanged {
                it?.let { btnSendCode.isEnabled = it.isValidEmail() }
            }

            otpCode.otpListener = object : OTPListener {
                override fun onInteractionListener() {
                    btnVerifyEmail.isEnabled = otpCode.otp?.length == 6
                }

                override fun onOTPComplete(otp: String) {
                    btnVerifyEmail.isEnabled = true
                }
            }
            btnSendCode.setOnClickListener {
                startLoading(btnSendCode)
                viewModel.sendCode(binding.tilEmail.getText())
            }
            btnVerifyEmail.setOnClickListener {
                startLoading(btnVerifyEmail)
                viewModel.validateEmail(otpCode.otp ?: "")
            }
            btnResend.setOnClickListener {
                viewModel.sendCode(binding.tilEmail.getText())
            }
            tvVerifyNote.setOnClickListener { openCustomTabUrl(getString(org.futo.circles.core.R.string.privacy_policy_url)) }
        }
    }

    private fun setupObservers() {
        viewModel.sendCodeLiveData.observeResponse(this,
            success = { validationCodeSentState() })

        viewModel.validateEmailLiveData.observeResponse(this, error = {
            showError(getString(R.string.invalid_validation_code))
        })
    }

    private fun validationCodeSentState() {
        showSuccess(getString(R.string.validation_code_sent_to_format, binding.tilEmail.getText()))
        with(binding) {
            tvVerifyTitle.text = getString(R.string.enter_the_code)
            tvVerifyDescription.text =
                getString(R.string.validation_code_sent_to_format, binding.tilEmail.getText())
            tilEmail.gone()
            btnSendCode.gone()
            tvVerifyNote.gone()
            btnVerifyEmail.visible()
            btnResend.visible()
        }
    }

}