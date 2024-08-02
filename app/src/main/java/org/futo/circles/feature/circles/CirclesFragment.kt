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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.databinding.FragmentRoomsBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.model.TimelineTypeArg
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.feature.circles.list.CirclesListAdapter
import org.futo.circles.feature.timeline.TimelineNavigator
import org.futo.circles.model.CircleListItem
import org.futo.circles.model.JoinedCircleListItem

@ExperimentalBadgeUtils
@AndroidEntryPoint
class CirclesFragment : BaseBindingFragment<FragmentRoomsBinding>(FragmentRoomsBinding::inflate),
    MenuProvider {

    private val viewModel by viewModels<CirclesViewModel>()
    private var listAdapter: CirclesListAdapter? = null
    private val navigator by lazy { CirclesNavigator(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listAdapter = null
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        menu.clear()
        inflater.inflate(R.menu.circles_tab_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.help -> navigator.navigateToExplanationDialog()
        }
        return true
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.circles_empty_message))
                setArrowVisible(true)
            })
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = CirclesListAdapter(
                onRoomClicked = { circleListItem -> onRoomListItemClicked(circleListItem) },
                onOpenInvitesClicked = { navigator.navigateToRoomRequests() },
                onAllPostsClicked = { navigator.navigateToAllPosts() }
            ).also { listAdapter = it }
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigator.navigateToCreateCircle() }
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