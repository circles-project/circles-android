package org.futo.circles.feature.sign_up.password

import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment
import org.futo.circles.databinding.FragmentPasswordBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.setIsVisible
import org.futo.circles.model.PasswordModeArg
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class PasswordFragment : ParentBackPressOwnerFragment(R.layout.fragment_password), HasLoadingState {

    private val args: PasswordFragmentArgs by navArgs()
    private val viewModel by viewModel<PasswordViewModel> {
        parametersOf(args.mode)
    }
    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentPasswordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnLogin.apply {
                setText(getString(if (isSignupMode()) R.string.set_password else R.string.log_in))
                setOnClickListener {
                    startLoading(btnLogin)
                    viewModel.loginWithPassword(tilPassword.getText())
                }
            }
            tilPassword.editText?.apply {
                doAfterTextChanged { onPasswordDataChanged() }
                imeOptions = if (isSignupMode()) EditorInfo.IME_ACTION_NEXT
                else EditorInfo.IME_ACTION_DONE
            }
            tilRepeatPassword.editText?.doAfterTextChanged {
                onPasswordDataChanged()
            }
            tilRepeatPassword.setIsVisible(isSignupMode())
        }
    }

    private fun isSignupMode() = when (args.mode) {
        PasswordModeArg.ReAuthBsSpekeSignup,
        PasswordModeArg.SignupPasswordStage,
        PasswordModeArg.SignupBsSpekeStage -> true
        else -> false
    }

    private fun setupObservers() {
        viewModel.passwordResponseLiveData.observeResponse(this)
        viewModel.minimumPasswordLengthLiveData.observeData(this) {
            binding.tvMinimumLength.text = getString(R.string.minimum_length_format, it)
            binding.tvMinimumLength.setIsVisible(it > 1)
        }
    }

    private fun onPasswordDataChanged() {
        val password = binding.tilPassword.getText()
        val repeat = binding.tilRepeatPassword.getText()
        val minLength = viewModel.minimumPasswordLengthLiveData.value ?: 1
        val isPasswordLengthCorrect = password.length >= minLength

        binding.btnLogin.isEnabled =
            if (isSignupMode()) isPasswordLengthCorrect && password == repeat
            else isPasswordLengthCorrect
    }
}