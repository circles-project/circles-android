package org.futo.circles.feature.people.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import org.futo.circles.R
import org.futo.circles.core.extensions.loadProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.DialogFragmentUserBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.people.user.list.UsersCirclesAdapter
import org.futo.circles.model.IgnoreUser
import org.futo.circles.model.UnfollowTimeline
import org.futo.circles.model.UnfollowUser
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
            onUnFollow = { timelineId ->
                withConfirmation(UnfollowTimeline()) {
                    viewModel.unFollowTimeline(timelineId)
                }
            }
        )
    }

    private var isUserIgnored = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        setupMenu()
        binding.rvCircles.apply {
            addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
            adapter = usersCirclesAdapter
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupMenu() {
        with(binding.toolbar) {
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            menu.findItem(R.id.unFollow).isVisible = viewModel.amIFollowingUser()
            menu.findItem(R.id.ignore).isVisible = !isUserIgnored
            menu.findItem(R.id.unIgnore).isVisible = isUserIgnored
            setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when (item.itemId) {
                    R.id.unFollow -> {
                        withConfirmation(UnfollowUser()) { viewModel.unFollowUser() }
                        true
                    }

                    R.id.ignore -> {
                        withConfirmation(IgnoreUser()) { viewModel.ignoreUser() }
                        true
                    }

                    R.id.unIgnore -> {
                        viewModel.unIgnoreUser()
                        true
                    }

                    else -> false
                }
            }
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
        viewModel.ignoreUserLiveData.observeResponse(this,
            success = {
                context?.let { showSuccess(it.getString(R.string.user_ignored), true) }
                onBackPressed()
            })
        viewModel.unIgnoreUserLiveData.observeResponse(this,
            success = {
                context?.let { showSuccess(it.getString(R.string.user_unignored)) }
            })
        viewModel.isUserIgnoredLiveData?.observeData(this) {
            isUserIgnored = it
            binding.toolbar.invalidateMenu()
        }
        viewModel.unFollowUserLiveData.observeResponse(this,
            success = { onBackPressed() })
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