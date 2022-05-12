package com.futo.circles.feature.circles

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.CirclesFragmentBinding
import com.futo.circles.extensions.bindToFab
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.feature.circles.list.CirclesListAdapter
import com.futo.circles.model.CircleListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class CirclesFragment : Fragment(R.layout.circles_fragment) {

    private val viewModel by viewModel<CirclesViewModel>()
    private val binding by viewBinding(CirclesFragmentBinding::bind)
    private val listAdapter by lazy {
        CirclesListAdapter(
            onCircleClicked = { circleListitem -> onCircleListItemClicked(circleListitem) },
            onInviteClicked = { circleListitem, isAccepted ->
                onInviteClicked(circleListitem, isAccepted)
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvCircles.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
            bindToFab(binding.fbAddCircle)
        }
        binding.fbAddCircle.setOnClickListener { navigateToCreateCircle() }
    }

    private fun setupObservers() {
        viewModel.circlesLiveData?.observeData(this) { listAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun onInviteClicked(room: CircleListItem, isAccepted: Boolean) {
        if (isAccepted) navigateToCirclePicker()
        else viewModel.rejectInvite(room.id)
    }

    private fun navigateToCirclePicker() {

    }

    private fun onCircleListItemClicked(room: CircleListItem) {
        findNavController().navigate(CirclesFragmentDirections.toTimeline(room.id))
    }

    private fun navigateToCreateCircle() {
        findNavController().navigate(CirclesFragmentDirections.toCreateRoomDialogFragment())
    }
}
