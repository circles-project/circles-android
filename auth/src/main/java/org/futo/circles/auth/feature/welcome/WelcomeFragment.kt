package org.futo.circles.auth.feature.welcome

import android.os.Bundle
import android.view.View
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.auth.databinding.FragmentWelcomeBinding
import org.futo.circles.core.base.fragment.BaseBindingFragment


@AndroidEntryPoint
class WelcomeFragment :
    BaseBindingFragment<FragmentWelcomeBinding>(FragmentWelcomeBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        with(binding) {

        }
    }

}