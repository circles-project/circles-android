package org.futo.circles.auth.feature.welcome

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentWelcomeBinding
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe


@AndroidEntryPoint
class WelcomeFragment :
    BaseBindingFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setupViews()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    private fun setupViews() {
        with(binding) {
            btnSignIn.setOnClickListener {
                findNavController().navigateSafe(WelcomeFragmentDirections.toLogInFragment())
            }
            btnSignUp.setOnClickListener {
                findNavController().navigateSafe(WelcomeFragmentDirections.toLogInFragment())
            }
        }
    }

}