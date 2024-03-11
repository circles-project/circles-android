package org.futo.circles.auth.feature.uia.stages.validate_email

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentValidateEmailBinding
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
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

    private val emailsAdapter by lazy {
        ArrayAdapter(
            requireContext(),
            org.futo.circles.core.R.layout.view_spinner_item,
            mutableListOf<String>()
        ).apply {
            setDropDownViewResource(org.futo.circles.core.R.layout.view_spinner_item)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tilEmail.apply {
                setIsVisible(isEnrollMode())
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
                    getEmailInput(),
                    cbEmailUpdates.isChecked && cbEmailUpdates.isVisible
                )
            }
            btnValidate.setOnClickListener {
                startLoading(btnValidate)
                viewModel.validateEmail(tilValidationCode.getText())
            }
            spEmail.apply {
                setIsVisible(!isEnrollMode())
                adapter = emailsAdapter
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
            emailsAdapter.apply {
                addAll(emails)
                notifyDataSetChanged()
                binding.btnSendCode.isEnabled = emails.isNotEmpty()
            }
        }
    }

    private fun getEmailInput(): String = if (isEnrollMode()) binding.tilEmail.getText() else
        emailsAdapter.getItem(binding.spEmail.selectedItemPosition) ?: ""

    private fun isEnrollMode(): Boolean {
        val currentStageKey = UIADataSourceProvider.getDataSourceOrThrow().getCurrentStageKey()
        return currentStageKey == UIADataSource.ENROLL_EMAIL_REQUEST_TOKEN_TYPE
                || currentStageKey == UIADataSource.ENROLL_EMAIL_SUBMIT_TOKEN_TYPE
    }

    private fun validationCodeSentState() {
        showSuccess(getString(R.string.validation_code_sent_to_format, getEmailInput()))
        binding.tilValidationCode.visible()
        binding.btnValidate.isEnabled =
            binding.tilValidationCode.editText?.text.isNullOrEmpty().not()
    }

}