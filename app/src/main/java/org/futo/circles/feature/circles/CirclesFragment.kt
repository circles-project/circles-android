package org.futo.circles.feature.circles

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
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
    BaseBindingFragment<FragmentCirclesBinding>(FragmentCirclesBinding::inflate){

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
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
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