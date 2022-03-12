package com.futo.circles.feature.validate_token

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.databinding.ValidateTokenFragmentBinding
import com.futo.circles.extensions.observeResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class ValidateTokenFragment : Fragment(R.layout.validate_token_fragment), HasLoadingState {

    override val fragment: Fragment = this
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
            btnValidate.setOnClickListener {
                startLoading(btnValidate)
                viewModel.validateToken(tilToken.editText?.text?.toString()?.trim() ?: "")
            }
        }
    }

    private fun setupObservers() {
        viewModel.validateLiveData.observeResponse(
            this,
            success = { }
        )
    }
}