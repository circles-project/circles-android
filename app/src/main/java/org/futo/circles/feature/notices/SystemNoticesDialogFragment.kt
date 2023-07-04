package org.futo.circles.feature.notices

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentSystemNoticesBinding
import org.futo.circles.feature.notices.list.SystemNoticesTimelineAdapter

@AndroidEntryPoint
class SystemNoticesDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentSystemNoticesBinding::inflate) {

    private val viewModel by viewModels<SystemNoticesTimelineViewModel>()

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
        binding.rvTimeline.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.timelineEventsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }
}