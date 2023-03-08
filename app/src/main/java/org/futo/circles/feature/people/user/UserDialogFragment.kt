package org.futo.circles.feature.people.user

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentUserBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.people.user.list.UsersCirclesAdapter
import org.futo.circles.mapping.notEmptyDisplayName
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.matrix.android.sdk.api.session.user.model.User


class UserDialogFragment : BaseFullscreenDialogFragment(DialogFragmentUserBinding::inflate) {

    private val args: UserDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<UserViewModel> {
        parametersOf(args.userId)
    }
    private val binding by lazy {
        getBinding() as DialogFragmentUserBinding
    }

    private val usersCirclesAdapter by lazy {
        UsersCirclesAdapter(
            onRequestFollow = { timelineId -> viewModel.requestFollowTimeline(timelineId) },
            onUnFollow = { timelineId -> viewModel.unFollow(timelineId) }
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvCircles.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = usersCirclesAdapter
        }
    }


    private fun setupObservers() {
        viewModel.userLiveData.observeData(this) { setupUserInfo(it) }
        viewModel.timelineLiveDataLiveData.observeData(this) {
            usersCirclesAdapter.submitList(it)
            binding.tvEmptyCirclesList.setIsVisible(it.isEmpty())
        }
        viewModel.requestFollowLiveData.observeResponse(this,
            success = { showSuccess(getString(R.string.request_sent)) })
    }

    private fun setupUserInfo(user: User) {
        with(binding) {
            toolbar.title = user.notEmptyDisplayName()
            tvUserId.text = user.userId
            tvUserName.text = user.notEmptyDisplayName()
            ivUser.loadProfileIcon(user.avatarUrl, user.notEmptyDisplayName())
            tvEmptyCirclesList.text =
                getString(R.string.not_following_any_circles_format, user.notEmptyDisplayName())
        }
    }

}