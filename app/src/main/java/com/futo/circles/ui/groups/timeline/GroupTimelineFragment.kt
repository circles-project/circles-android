package com.futo.circles.ui.groups.timeline

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.base.BaseRvDecoration
import com.futo.circles.databinding.GroupTimelineFragmentBinding
import com.futo.circles.extensions.bindToFab
import com.futo.circles.extensions.dimen
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.setToolbarTitle
import com.futo.circles.model.Post
import com.futo.circles.ui.groups.timeline.list.GroupPostViewHolder
import com.futo.circles.ui.groups.timeline.list.GroupTimelineAdapter
import com.futo.circles.ui.view.GroupPostListener
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GroupTimelineFragment : Fragment(R.layout.group_timeline_fragment), GroupPostListener {

    private val args: GroupTimelineFragmentArgs by navArgs()
    private val viewModel by viewModel<GroupTimelineViewModel> { parametersOf(args.roomId) }
    private val binding by viewBinding(GroupTimelineFragmentBinding::bind)

    private val listAdapter by lazy {
        GroupTimelineAdapter(this) { viewModel.loadMore() }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvGroupTimeline.apply {
            adapter = listAdapter
            addItemDecoration(
                BaseRvDecoration.OffsetDecoration<GroupPostViewHolder>(
                    offset = context.dimen(R.dimen.group_post_item_offset)
                )
            )
            bindToFab(binding.fbCreatePost)
        }
        setupObservers()
    }

    private fun setupObservers() {
        with(viewModel) {
            titleLiveData.observeData(this@GroupTimelineFragment) { title -> setToolbarTitle(title) }
            timelineEventsLiveData.observeData(this@GroupTimelineFragment, ::setTimelineList)
        }
    }

    private fun setTimelineList(list: List<Post>) {
        listAdapter.submitList(list)
    }

    override fun onShowRepliesClicked(eventId: String) {
        viewModel.toggleRepliesVisibilityFor(eventId)
    }
}