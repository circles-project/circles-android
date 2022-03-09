package com.futo.circles.feature.select_users

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.SelectUsersFragmentBinding
import com.futo.circles.extensions.getQueryTextChangeStateFlow
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.setVisibility
import com.futo.circles.feature.select_users.list.search.InviteMembersSearchListAdapter
import com.futo.circles.feature.select_users.list.selected.SelectedUsersListAdapter
import com.futo.circles.model.UserListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

interface SelectUsersListener {
    fun onUserSelected(users: List<UserListItem>)
}

class SelectUsersFragment : Fragment(R.layout.select_users_fragment) {

    private val roomId: String? by lazy {
        arguments?.getString(ROOM_ID)
    }

    private val viewModel by viewModel<SelectUsersViewModel> { parametersOf(roomId) }
    private val binding by viewBinding(SelectUsersFragmentBinding::bind)

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

    fun getSelectedUsers(): List<UserListItem> =
        viewModel.selectedUsersLiveData.value ?: emptyList()

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
            binding.selectedUserDivider.setVisibility(items.isNotEmpty())
            selectUsersListener?.onUserSelected(items)
        }
    }

    companion object {
        private const val ROOM_ID = "roomId"
        fun create(roomId: String?) = SelectUsersFragment().apply {
            arguments = bundleOf(ROOM_ID to roomId)
        }
    }
}