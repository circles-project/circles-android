package org.futo.circles.feature.people

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuProvider
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.getQueryTextChangeStateFlow
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.FragmentPeopleBinding
import org.futo.circles.feature.people.list.PeopleAdapter
import org.matrix.android.sdk.api.session.user.model.User

@AndroidEntryPoint
class PeopleFragment : BaseBindingFragment<FragmentPeopleBinding>(FragmentPeopleBinding::inflate),
    MenuProvider {

    private val viewModel by viewModels<PeopleViewModel>()
    private val navigator by lazy { PeopleNavigator(this) }

    private val peopleAdapter by lazy {
        PeopleAdapter(onUserClicked = { userId -> navigator.navigateToUserPage(userId) },
            onOpenRequestsClicked = { navigator.navigateToInvites() },
            onCategoryClicked = { categoryType ->
                navigator.navigateToPeopleCategoryPage(categoryType)
            })
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
        (menu.findItem(R.id.search).actionView as? SearchView)?.getQueryTextChangeStateFlow { query ->
            binding.lProfileContainer.setIsVisible(query.isEmpty())
        }?.let {
            viewModel.initSearchListener(it)
        }
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean = true

    private fun setupViews() {
        with(binding) {
            rvUsers.apply {
                setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                    setText(getString(R.string.no_results))
                })
                adapter = peopleAdapter
            }
            ivEditProfile.setOnClickListener { navigator.navigateToEditProfile() }
            ivShareProfile.setOnClickListener { navigator.navigateToShareProfile(viewModel.getSharedCircleSpaceId()) }
        }
    }

    private fun setupObservers() {
        viewModel.peopleLiveData.observeData(this) { items ->
            peopleAdapter.submitList(items)
        }
        viewModel.profileLiveData.observeData(this) { user ->
            user.getOrNull()?.let { bindProfile(it) }
        }
    }

    private fun bindProfile(user: User) {
        with(binding) {
            ivProfile.loadUserProfileIcon(user.avatarUrl, user.userId)
            tvUserName.text = user.notEmptyDisplayName()
            tvUserId.text = user.userId
        }
    }

}