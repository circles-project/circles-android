package org.futo.circles.feature.direct.tab

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.FragmentDirectMessagesBinding
import org.futo.circles.feature.direct.tab.list.DMsListAdapter

@AndroidEntryPoint
class DMFragment :
    BaseBindingFragment<FragmentDirectMessagesBinding>(FragmentDirectMessagesBinding::inflate) {

    private val viewModel by viewModels<DMViewModel>()
    private val listAdapter by lazy {
        DMsListAdapter(
            onDmItemClicked = { dmListItem ->
                findNavController().navigateSafe(
                    DMFragmentDirections.toDmTimelineNavGraph(dmListItem.id)
                )
            },
            onOpenInvitesClicked = {
                findNavController().navigateSafe(DMFragmentDirections.toRoomRequests())
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvDirectMessages.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.invite_users_for_direct_messages))
                setArrowVisible(true)
            })
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = listAdapter
            bindToFab(binding.fbAddUser)
        }
        binding.fbAddUser.setOnClickListener {
            findNavController().navigateSafe(DMFragmentDirections.toCreateDMDialogFragment())
        }
    }

    private fun setupObservers() {
        viewModel.dmsLiveData.observeData(this) { listAdapter.submitList(it) }
    }
}