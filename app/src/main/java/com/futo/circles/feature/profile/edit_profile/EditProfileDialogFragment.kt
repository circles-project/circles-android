package com.futo.circles.feature.profile.edit_profile

import android.os.Bundle
import android.view.View
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.EditProfileDialogFragmentBinding

class EditProfileDialogFragment :
    BaseFullscreenDialogFragment(EditProfileDialogFragmentBinding::inflate) {

    private val binding by lazy {
        getBinding() as EditProfileDialogFragmentBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }
}