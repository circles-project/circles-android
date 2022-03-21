package com.futo.circles.feature.validate_email

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.databinding.ValidateEmailFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ValidateEmailFragment : Fragment(R.layout.validate_email_fragment), HasLoadingState {

    override val fragment: Fragment = this
    private val binding by viewBinding(ValidateEmailFragmentBinding::bind)
    private val viewModel by viewModel<ValidateEmailViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}