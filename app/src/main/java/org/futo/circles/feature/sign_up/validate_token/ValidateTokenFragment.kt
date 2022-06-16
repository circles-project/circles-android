package org.futo.circles.feature.sign_up.validate_token

import android.os.Bundle
import android.view.View
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.fragment.HasLoadingState
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment
import org.futo.circles.databinding.ValidateTokenFragmentBinding
import org.futo.circles.extensions.getText
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ValidateTokenFragment : ParentBackPressOwnerFragment(R.layout.validate_token_fragment),
    HasLoadingState {

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