package org.futo.circles.feature.people

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.databinding.FragmentPeopleBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.observeResponse
import org.futo.circles.extensions.withConfirmation
import org.futo.circles.feature.people.list.PeopleAdapter
import org.futo.circles.model.ConfirmationType
import org.futo.circles.model.PeopleUserListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PeopleFragment : Fragment(R.layout.fragment_people) {

    private val viewModel by viewModel<PeopleViewModel>()
    private val binding by viewBinding(FragmentPeopleBinding::bind)

    private val peopleAdapter by lazy {
        PeopleAdapter(
            onUserClicked = { user -> navigateToUserPage(user) },
            onIgnore = { user, ignore -> handleIgnoreClicked(user, ignore) },
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

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
        viewModel.ignoreUserLiveData.observeResponse(this)
    }

    private fun navigateToUserPage(user: PeopleUserListItem) {
        findNavController().navigate(PeopleFragmentDirections.toUserFragment(user.id))
    }

    private fun handleIgnoreClicked(user: PeopleUserListItem, ignore: Boolean) {
        if (ignore) withConfirmation(ConfirmationType.IGNORE_USER) { viewModel.ignoreUser(user.id) }
        else viewModel.unIgnoreUser(user.id)
    }
}