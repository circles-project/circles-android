package com.futo.circles.feature.home.profile

import android.os.Bundle
import android.view.View
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.ProfileDialogFragmentBinding

class ProfileDialogFragment : BaseFullscreenDialogFragment(ProfileDialogFragmentBinding::inflate) {

    private val binding by lazy {
        getBinding() as ProfileDialogFragmentBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }
}