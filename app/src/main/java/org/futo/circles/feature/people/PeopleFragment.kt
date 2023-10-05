package org.futo.circles.feature.people

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.NetworkObserver
import org.futo.circles.core.extensions.getQueryTextChangeStateFlow
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.FragmentPeopleBinding
import org.futo.circles.feature.people.list.PeopleAdapter

@AndroidEntryPoint
class PeopleFragment : Fragment(R.layout.fragment_people), MenuProvider {

    private val viewModel by viewModels<PeopleViewModel>()
    private val binding by viewBinding(FragmentPeopleBinding::bind)

    private val peopleAdapter by lazy {
        PeopleAdapter(
            onUserClicked = { userId -> navigateToUserPage(userId) },
            onRequestClicked = { userId, isAccepted ->
                viewModel.onFollowRequestAnswered(userId, isAccepted)
            },
            onUnIgnore = { userId -> viewModel.unIgnoreUser(userId) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.people_tab_menu, menu)
        (menu.findItem(R.id.search).actionView as? SearchView)?.getQueryTextChangeStateFlow()?.let {
            viewModel.initSearchListener(it)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = true

    private fun setupViews() {
        binding.rvUsers.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.people_empty_message))
                setArrowVisible(false)
            })
            adapter = peopleAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it, listOf(binding.rvUsers)) }
        viewModel.peopleLiveData.observeData(this) { items ->
            peopleAdapter.submitList(items)
        }
        viewModel.followUserLiveData.observeResponse(this,
            success = { showSuccess(getString(R.string.request_sent)) })
        viewModel.unIgnoreUserLiveData.observeResponse(this)
        viewModel.followUserRequestLiveData.observeResponse(this)
    }

    private fun navigateToUserPage(userId: String) {
        findNavController().navigateSafe(PeopleFragmentDirections.toUserFragment(userId))
    }
}