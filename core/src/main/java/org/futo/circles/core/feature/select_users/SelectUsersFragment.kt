package org.futo.circles.core.feature.select_users

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.databinding.FragmentRoomsBinding
import org.futo.circles.core.databinding.FragmentSelectUsersBinding
import org.futo.circles.core.extensions.getQueryTextChangeStateFlow
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.select_users.list.search.InviteMembersSearchListAdapter
import org.futo.circles.core.feature.select_users.list.selected.SelectedUsersListAdapter
import org.futo.circles.core.model.NoResultsItem

@AndroidEntryPoint
class SelectUsersFragment : Fragment() {

    private val viewModel by viewModels<SelectUsersViewModel>()
    private var _binding: FragmentSelectUsersBinding? = null
    private val binding get() = _binding!!

    private val searchListAdapter by lazy { InviteMembersSearchListAdapter(viewModel::onUserSelected) }
    private val selectedUsersListAdapter by lazy { SelectedUsersListAdapter(viewModel::onUserSelected) }

    private var selectUsersListener: SelectUsersListener? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelectUsersBinding.inflate(inflater, container, false)
        return binding.root
    }

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
            searchListAdapter.submitList(items)
            handleSelectUsersMessageVisibility()
        }
        viewModel.selectedUsersLiveData.observeData(this) { items ->
            selectedUsersListAdapter.submitList(items)
            selectUsersListener?.onUserSelected(items.map { it.id })
            handleSelectUsersMessageVisibility()
        }
    }

    private fun handleSelectUsersMessageVisibility() {
        binding.lSelectedItems.setIsVisible(
            viewModel.searchUsersLiveData.value?.firstOrNull { it is NoResultsItem } == null ||
                    viewModel.selectedUsersLiveData.value?.isNotEmpty() == true
        )
        binding.tvSelectedUsersPlaceholder.setIsVisible(
            viewModel.selectedUsersLiveData.value?.isEmpty() == true
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ROOM_ID = "roomId"
        fun create(roomId: String?) = SelectUsersFragment().apply {
            arguments = bundleOf(ROOM_ID to roomId)
        }
    }
}