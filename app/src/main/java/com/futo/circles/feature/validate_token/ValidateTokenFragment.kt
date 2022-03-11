package com.futo.circles.feature.validate_token

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.ValidateTokenFragmentBinding
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.setEnabledViews
import com.futo.circles.extensions.showError
import org.koin.androidx.viewmodel.ext.android.viewModel

class ValidateTokenFragment : Fragment(R.layout.validate_token_fragment) {

    private val binding by viewBinding(ValidateTokenFragmentBinding::bind)
    private val viewModel by viewModel<ValidateTokenViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            tilToken.editText?.doAfterTextChanged {
                it?.let { btnValidate.setButtonEnabled(it.isNotEmpty()) }
            }
            btnValidate.setOnClickWithLoading {
                setLoadingState(true)
                viewModel.validateToken(tilToken.editText?.text?.toString()?.trim() ?: "")
            }
        }
    }

    private fun setupObservers() {
        viewModel.validateLiveData.observeResponse(
            this,
            onRequestInvoked = { setLoadingState(false) },
            success = {  },
            error = { showError(it) }
        )
    }

    private fun setLoadingState(isLoading: Boolean) {
        setEnabledViews(!isLoading)
        binding.btnValidate.setIsLoading(isLoading)
    }
}