package org.futo.circles.feature.notices

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentSystemNoticesBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.feature.notices.list.SystemNoticesTimelineAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SystemNoticesDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentSystemNoticesBinding::inflate) {

    private val args: SystemNoticesDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<SystemNoticesTimelineViewModel> {
        parametersOf(args.roomId, args.type)
    }

    private val binding by lazy {
        getBinding() as DialogFragmentSystemNoticesBinding
    }

    private val listAdapter by lazy {
        SystemNoticesTimelineAdapter { viewModel.loadMore() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        binding.rvTimeline.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.timelineEventsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }
}