package com.futo.circles.feature.people

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.PeopleFragmentBinding
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.observeResponse
import com.futo.circles.extensions.showDialog
import com.futo.circles.feature.people.list.PeopleAdapter
import com.futo.circles.model.PeopleUserListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PeopleFragment : Fragment(R.layout.people_fragment) {

    private val viewModel by viewModel<PeopleViewModel>()
    private val binding by viewBinding(PeopleFragmentBinding::bind)

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

    }

    private fun handleIgnoreClicked(user: PeopleUserListItem, ignore: Boolean) {
        if (ignore) showIgnoreConfirmation(user.id)
        else viewModel.unIgnoreUser(user.id)
    }

    private fun showIgnoreConfirmation(userId: String) {
        showDialog(
            titleResIdRes = R.string.ignore,
            messageResId = R.string.ignore_user_message,
            positiveButtonRes = R.string.ignore,
            negativeButtonVisible = true,
            positiveAction = { viewModel.ignoreUser(userId) }
        )
    }

}