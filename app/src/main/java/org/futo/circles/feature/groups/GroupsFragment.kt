package org.futo.circles.feature.groups

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
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.databinding.FragmentRoomsBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.feature.explanation.CirclesExplanationDialog
import org.futo.circles.feature.groups.list.GroupsListAdapter
import org.futo.circles.model.GroupListItem

@AndroidEntryPoint
class GroupsFragment : BaseBindingFragment<FragmentRoomsBinding>(FragmentRoomsBinding::inflate),
    MenuProvider {

    private val viewModel by viewModels<GroupsViewModel>()
    private val listAdapter by lazy {
        GroupsListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onOpenInvitesClicked = {
                findNavController().navigateSafe(GroupsFragmentDirections.toRoomRequests())
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }


    @SuppressLint("RestrictedApi")
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        menu.clear()
        inflater.inflate(R.menu.circles_tab_menu, menu)
    }

    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.help -> CirclesExplanationDialog(requireContext(), CircleRoomTypeArg.Group).show()
        }
        return true
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.groups_empty_message))
                setArrowVisible(true)
            })
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) { listAdapter.submitList(it) }
    }


    private fun onRoomListItemClicked(room: GroupListItem) {
        findNavController().navigateSafe(GroupsFragmentDirections.toTimeline(room.id))
    }

    private fun navigateToCreateRoom() {
        findNavController().navigateSafe(GroupsFragmentDirections.toCreateGroupDialogFragment())
    }
}