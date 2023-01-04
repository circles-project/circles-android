package org.futo.circles.feature.people

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.databinding.FragmentPeopleBinding
import org.futo.circles.extensions.getQueryTextChangeStateFlow
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.showSuccess
import org.futo.circles.feature.people.list.PeopleAdapter
import org.futo.circles.model.PeopleListItem
import org.koin.androidx.viewmodel.ext.android.viewModel


class PeopleFragment : Fragment(R.layout.fragment_people), MenuProvider {

    private val viewModel by viewModel<PeopleViewModel>()
    private val binding by viewBinding(FragmentPeopleBinding::bind)

    private val peopleAdapter by lazy {
        PeopleAdapter(
            onUserClicked = { user -> navigateToUserPage(user) },
            onFollow = { user -> viewModel.followUser(user) },
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.bottom_nav_toolbar_menu, menu)
        (menu.findItem(R.id.search).actionView as? SearchView)?.getQueryTextChangeStateFlow()?.let {
            viewModel.initSearchListener(it)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = true

    private fun setupViews() {
        binding.rvUsers.apply {
            adapter = peopleAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewModel.peopleLiveData.observeData(this) { items ->
            peopleAdapter.submitList(items)
        }
        viewModel.followUserLiveData.observeResponse(this,
            success = { showSuccess(getString(R.string.request_sent)) })
    }

    private fun navigateToUserPage(user: PeopleListItem) {
        findNavController().navigate(PeopleFragmentDirections.toUserFragment(user.id))
    }
}