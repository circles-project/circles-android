package com.futo.circles.feature.sign_up_type

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.SelectSignUpTypeFragmentBinding

class SelectSignUpTypeFragment : Fragment(R.layout.select_sign_up_type_fragment) {

    private val binding by viewBinding(SelectSignUpTypeFragmentBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnToken.setOnClickListener { navigateToTokenValidation() }
    }

    private fun navigateToTokenValidation() {
        findNavController()
            .navigate(SelectSignUpTypeFragmentDirections.toValidateTokenFragment())
    }

}