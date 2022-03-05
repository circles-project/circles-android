package com.futo.circles.feature.create_group

import android.os.Bundle
import android.view.View
import com.futo.circles.base.BaseFullscreenDialogFragment
import com.futo.circles.databinding.CreateGroupDialogFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateGroupDialogFragment :
    BaseFullscreenDialogFragment(CreateGroupDialogFragmentBinding::inflate) {

    private val viewModel by viewModel<CreateGroupViewModel>()


    private val binding by lazy {
        getBinding() as CreateGroupDialogFragmentBinding
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

}