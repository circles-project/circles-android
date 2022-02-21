package com.futo.circles.ui.groups

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.GroupsFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.ui.groups.list.GroupsListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.matrix.android.sdk.api.session.room.model.RoomSummary


class GroupsFragment : Fragment(R.layout.groups_fragment) {

    private val viewModel by viewModel<GroupsViewModel>()
    private val binding by viewBinding(GroupsFragmentBinding::bind)
    private val listAdapter by lazy {
        GroupsListAdapter(
            viewModel.getContentResolver(),
            ::onGroupListItemClicked
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvGroups.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
        }

        viewModel.groupsLiveData?.observeData(this, ::setGroupsList)
    }

    private fun setGroupsList(list: List<RoomSummary>) {
        listAdapter.submitList(list)
    }

    private fun onGroupListItemClicked(room: RoomSummary) {
        findNavController().navigate(
            GroupsFragmentDirections.actionGroupsFragment2ToGroupTimelineFragment(
                room.roomId
            )
        )
    }
}