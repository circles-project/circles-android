package org.futo.circles.auth.feature.uia.stages.password

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentPasswordBinding
import org.futo.circles.auth.feature.uia.UIADataSource
import org.futo.circles.auth.feature.uia.UIADataSourceProvider
import org.futo.circles.auth.feature.uia.stages.password.confirmation.SetupPasswordWarningDialog
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showError

@AndroidEntryPoint
class PasswordFragment :
    ParentBackPressOwnerFragment<FragmentPasswordBinding>(FragmentPasswordBinding::inflate),
    HasLoadingState {

    private val viewModel by viewModels<PasswordViewModel>()
    override val fragment: Fragment = this
    private val passphraseWarningDialog by lazy { SetupPasswordWarningDialog(requireContext()) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        if (!isSignupMode()) viewModel.getCredentials(requireContext())
    }

    override fun onResume() {
        super.onResume()
        showPasswordWarningIfNeeded()
        onPasswordDataChanged()
    }

    private fun setupViews() {
        with(binding) {
            btnLogin.apply {
                setText(getString(if (isSignupMode()) R.string.set_passphrase else R.string.log_in))
                setOnClickListener {
                    startLoading(btnLogin)
                    viewModel.processPasswordStage(
                        tilPassword.getText(),
                        isSignupMode(),
                        requireContext()
                    )
                }
            }
            tvPasswordTitle.text =
                getString(if (isSignupMode()) R.string.choose_a_passphrase else R.string.enter_your_passphrase)
            tilPassword.editText?.apply {
                doAfterTextChanged {
                    if (isSignupMode()) vPasswordStrength.calculateStrength(tilPassword.getText())
                    onPasswordDataChanged()
                }
                imeOptions = if (isSignupMode()) EditorInfo.IME_ACTION_NEXT
                else EditorInfo.IME_ACTION_DONE
            }
            tilRepeatPassword.editText?.doAfterTextChanged { onPasswordDataChanged() }
            tilRepeatPassword.setIsVisible(isSignupMode())
        }
    }

    private fun isSignupMode(): Boolean {
        val currentStageKey = UIADataSourceProvider.getDataSourceOrThrow().getCurrentStageKey()
        return currentStageKey == UIADataSource.ENROLL_PASSWORD_TYPE
                || currentStageKey == UIADataSource.ENROLL_BSSPEKE_OPRF_TYPE
                || currentStageKey == UIADataSource.ENROLL_BSSPEKE_SAVE_TYPE
    }

    private fun setupObservers() {
        viewModel.passwordResponseLiveData.observeResponse(this,
            error = { showError(getString(R.string.the_password_you_entered_is_incorrect)) })
        viewModel.passwordSelectedEventLiveData.observeData(this) {
            startLoading(binding.btnLogin)
            binding.etPassword.setText(it)
        }
    }

    private fun onPasswordDataChanged() {
        val password = binding.tilPassword.getText()
        val repeat = binding.tilRepeatPassword.getText()
        binding.btnLogin.isEnabled = if (isSignupMode()) {
            binding.vPasswordStrength.isPasswordStrong() && password == repeat && password.isNotEmpty() && !binding.btnLogin.isLoading
        } else password.isNotEmpty() && !binding.btnLogin.isLoading
    }

    private fun showPasswordWarningIfNeeded() {
        if (isSignupMode() && viewModel.isPasswordWarningConfirmed().not()) {
            passphraseWarningDialog.apply {
                setOnDismissListener { viewModel.confirmPasswordWarning() }
                show()
            }
        }
    }
}