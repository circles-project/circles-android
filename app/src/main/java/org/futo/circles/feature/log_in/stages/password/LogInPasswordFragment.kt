package org.futo.circles.feature.log_in.stages.password

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.databinding.FragmentLoginPasswordBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class LogInPasswordFragment : Fragment(R.layout.fragment_login_password), HasLoadingState {

    override val fragment: Fragment = this
    private val viewModel by viewModel<LoginPasswordViewModel>()
    private val binding by viewBinding(FragmentLoginPasswordBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            btnLogin.setOnClickListener {
                startLoading(btnLogin)
                viewModel.loginWithPassword(tilPassword.getText())
            }
            tilPassword.editText?.doAfterTextChanged {
                btnLogin.isEnabled = tilPassword.getText().isNotEmpty()
            }
        }
    }

    private fun setupObservers() {
        viewModel.loginResponseLiveData.observeResponse(this)
    }
}