package org.futo.circles.auth.feature.sign_up.validate_token

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentValidateTokenBinding
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showDialog
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment

@AndroidEntryPoint
class ValidateTokenFragment : ParentBackPressOwnerFragment(R.layout.fragment_validate_token),
    HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(FragmentValidateTokenBinding::bind)
    private val viewModel by viewModels<ValidateTokenViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tilToken.editText?.doAfterTextChanged {
                it?.let { btnValidate.isEnabled = it.isNotEmpty() }
            }
            tilToken.setEndIconOnClickListener {
                showDialog(
                    R.string.sign_up_token,
                    R.string.sign_up_token_explanation
                )
            }
            btnValidate.setOnClickListener {
                startLoading(btnValidate)
                viewModel.validateToken(tilToken.getText())
            }
        }
    }

    private fun setupObservers() {
        viewModel.validateLiveData.observeResponse(this)
    }
}