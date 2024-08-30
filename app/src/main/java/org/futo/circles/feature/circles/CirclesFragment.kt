package org.futo.circles.feature.circles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.FragmentCirclesBinding
import org.futo.circles.feature.circles.list.CirclesListAdapter
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.JoinedCircleListItem

@ExperimentalBadgeUtils
@AndroidEntryPoint
class CirclesFragment :
    BaseBindingFragment<FragmentCirclesBinding>(FragmentCirclesBinding::inflate) {

    private val viewModel by viewModels<CirclesViewModel>()
    private var listAdapter: CirclesListAdapter? = null
    private val navigator by lazy { CirclesNavigator(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter = null
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.circles_empty_message))
            })
            adapter = CirclesListAdapter(
                onRoomClicked = { circleListItem -> onRoomListItemClicked(circleListItem) },
                onOpenInvitesClicked = { navigator.navigateToRoomRequests() },
                onAllPostsClicked = { navigator.navigateToAllPosts() }
            ).also { listAdapter = it }
        }
        binding.ivCreateCircle.setOnClickListener { navigator.navigateToCreateCircle() }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) {
            listAdapter?.submitList(it)
            binding.rvRooms.notifyItemsChanged()
        }
    }

    private fun onRoomListItemClicked(circleListItem: CircleListItem) {
        val circle = (circleListItem as? JoinedCircleListItem) ?: return
        navigator.navigateToCircleTimeline(circle.id)
    }
}