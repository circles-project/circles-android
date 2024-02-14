package org.futo.circles.feature.ignored

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.showUnIgnoreConfirmationDialog
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.databinding.DialogFragmentIgnoredUsersBinding
import org.futo.circles.feature.ignored.list.IgnoredUsersAdapter

@AndroidEntryPoint
class IgnoredUsersDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentIgnoredUsersBinding::inflate) {

    private val viewModel by viewModels<IgnoredUsersViewModel>()

    private val binding by lazy {
        getBinding() as DialogFragmentIgnoredUsersBinding
    }

    private val usersAdapter by lazy {
        IgnoredUsersAdapter(
            onUnIgnore = { userId ->
                showUnIgnoreConfirmationDialog {
                    viewModel.unIgnoreUser(userId, it)
                }
            }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvIgnoredUsers.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.ignored_users_empty_message))
            })
            adapter = usersAdapter
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
        }
    }

    private fun setupObservers() {
        viewModel.ignoredUsersLiveData.observeData(this) {
            usersAdapter.submitList(it)
        }
        viewModel.unIgnoreUserLiveData.observeResponse(this,
            success = { shouldRestart ->
                context?.let { showSuccess(it.getString(org.futo.circles.core.R.string.user_unignored)) }
                if (shouldRestart) {
                    activity?.let {
                        LauncherActivityUtils.clearSessionAndRestart(it, it.intent)
                    }
                }
            })
    }

}
