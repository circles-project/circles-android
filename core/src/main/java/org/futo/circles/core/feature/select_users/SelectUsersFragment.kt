package org.futo.circles.core.feature.select_users

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.databinding.FragmentSelectUsersBinding
import org.futo.circles.core.extensions.getQueryTextChangeStateFlow
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.model.NoResultsItem
import org.futo.circles.core.feature.select_users.list.search.InviteMembersSearchListAdapter
import org.futo.circles.core.feature.select_users.list.selected.SelectedUsersListAdapter

@AndroidEntryPoint
class SelectUsersFragment : Fragment(R.layout.fragment_select_users) {

    private val viewModel by viewModels<SelectUsersViewModel>()
    private val binding by viewBinding(FragmentSelectUsersBinding::bind)

    private val searchListAdapter by lazy { InviteMembersSearchListAdapter(viewModel::onUserSelected) }
    private val selectedUsersListAdapter by lazy { SelectedUsersListAdapter(viewModel::onUserSelected) }

    private var selectUsersListener: SelectUsersListener? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLists()
        setupObservers()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        selectUsersListener = (parentFragment as? SelectUsersListener)
    }

    fun getSelectedUsersIds(): List<String> =
        viewModel.selectedUsersLiveData.value?.map { it.id } ?: emptyList()

    private fun setupLists() {
        binding.rvUsers.adapter = searchListAdapter
        viewModel.initSearchListener(binding.searchView.getQueryTextChangeStateFlow())
        binding.rvSelectedUsers.adapter = selectedUsersListAdapter
    }

    private fun setupObservers() {
        viewModel.searchUsersLiveData.observeData(this) { items ->
            binding.lSelectedItems.setIsVisible(items.firstOrNull { it is NoResultsItem } == null)
            searchListAdapter.submitList(items)
        }
        viewModel.selectedUsersLiveData.observeData(this) { items ->
            selectedUsersListAdapter.submitList(items)
            binding.tvSelectedUsersPlaceholder.setIsVisible(items.isEmpty())
            selectUsersListener?.onUserSelected(items.map { it.id })
        }
    }

    companion object {
        private const val ROOM_ID = "roomId"
        fun create(roomId: String?) = SelectUsersFragment().apply {
            arguments = bundleOf(ROOM_ID to roomId)
        }
    }
}