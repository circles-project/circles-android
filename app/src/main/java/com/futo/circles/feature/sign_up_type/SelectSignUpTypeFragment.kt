package com.futo.circles.feature.sign_up_type

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.core.HasLoadingState
import com.futo.circles.databinding.SelectSignUpTypeFragmentBinding
import com.futo.circles.extensions.observeResponse
import org.koin.androidx.viewmodel.ext.android.viewModel

class SelectSignUpTypeFragment : Fragment(R.layout.select_sign_up_type_fragment), HasLoadingState {

    override val fragment: Fragment = this

    private val binding by viewBinding(SelectSignUpTypeFragmentBinding::bind)
    private val viewModel by viewModel<SelectSignUpTypeViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.clearSubtitle()

        binding.btnToken.setOnClickListener {
            startLoading(binding.btnToken)
            viewModel.startSignUp()
        }

        viewModel.startSignUpEventLiveData.observeResponse(this)
    }


}