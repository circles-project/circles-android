package org.futo.circles.auth.feature.uia.stages.validate_token

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.R
import org.futo.circles.auth.databinding.FragmentValidateTokenBinding
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.base.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.extensions.getText
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showDialog

@AndroidEntryPoint
class ValidateTokenFragment : ParentBackPressOwnerFragment(FragmentValidateTokenBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val binding by lazy {
        getBinding() as FragmentValidateTokenBinding
    }
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