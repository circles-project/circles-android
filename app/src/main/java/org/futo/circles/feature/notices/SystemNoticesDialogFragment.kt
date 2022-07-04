package org.futo.circles.feature.notices

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.SystemNoticesDialogFragmentBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SystemNoticesDialogFragment :
    BaseFullscreenDialogFragment(SystemNoticesDialogFragmentBinding::inflate) {

    private val args: SystemNoticesDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<SystemNoticesTimelineViewModel> {
        parametersOf(args.roomId, args.type)
    }

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