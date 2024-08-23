package org.futo.circles.settings.feature.ignored_users

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.model.UnIgnoreUser
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.settings.R
import org.futo.circles.settings.databinding.DialogFragmentIgnoredUsersBinding
import org.futo.circles.settings.feature.ignored_users.list.IgnoredUsersAdapter

@AndroidEntryPoint
class IgnoredUsersDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentIgnoredUsersBinding>(
        DialogFragmentIgnoredUsersBinding::inflate
    ) {

    private val viewModel by viewModels<IgnoredUsersViewModel>()

    private val usersAdapter by lazy {
        IgnoredUsersAdapter(
            onUnIgnore = { userId ->
                withConfirmation(UnIgnoreUser()) { viewModel.unIgnoreUser(userId) }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        with(binding) {
            rvUsers.apply {
                setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                    setText(getString(R.string.ignored_users_empty_message))
                })
                adapter = usersAdapter
            }
        }
    }

    private fun setupObservers() {
        viewModel.usersLiveData.observeData(this) { usersAdapter.submitList(it) }
        viewModel.unIgnoreUserLiveData.observeResponse(this,
            success = {
                (activity as? AppCompatActivity)?.let {
                    LauncherActivityUtils.clearCacheAndRestart(it)
                }
            })
    }

}
