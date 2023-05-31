package org.futo.circles.core.select_users

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.core.R
import org.futo.circles.core.databinding.FragmentSelectUsersBinding
import org.futo.circles.core.extensions.getQueryTextChangeStateFlow
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.select_users.list.search.InviteMembersSearchListAdapter
import org.futo.circles.core.select_users.list.selected.SelectedUsersListAdapter
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class SelectUsersFragment : Fragment(R.layout.fragment_select_users) {

    private val roomId: String? by lazy {
        arguments?.getString(ROOM_ID)
    }

    private val viewModel by viewModel<SelectUsersViewModel> { parametersOf(roomId) }
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
            searchListAdapter.submitList(items)
        }
        viewModel.selectedUsersLiveData.observeData(this) { items ->
            selectedUsersListAdapter.submitList(items)
            binding.selectedUserDivider.setIsVisible(items.isNotEmpty())
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