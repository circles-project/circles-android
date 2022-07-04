package org.futo.circles.feature.notices

import android.os.Bundle
import android.view.View
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.SystemNoticesDialogFragmentBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class SystemNoticesDialogFragment :
    BaseFullscreenDialogFragment(SystemNoticesDialogFragmentBinding::inflate) {

    private val binding by lazy {
        getBinding() as SystemNoticesDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    private fun setupObservers() {

    }
}