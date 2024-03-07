package org.futo.circles.core.feature.user

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentUserBinding
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setEnabledChildren
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.user.list.UsersCirclesAdapter
import org.futo.circles.core.model.IgnoreUser
import org.futo.circles.core.model.UnIgnoreUser
import org.futo.circles.core.model.UnfollowTimeline
import org.futo.circles.core.model.UnfollowUser
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.matrix.android.sdk.api.session.user.model.User

@AndroidEntryPoint
class UserDialogFragment : BaseFullscreenDialogFragment(DialogFragmentUserBinding::inflate) {

    private val viewModel by viewModels<UserViewModel>()
    private val binding by lazy {
        getBinding() as DialogFragmentUserBinding
    }

    private val usersCirclesAdapter by lazy {
        UsersCirclesAdapter(
            onRequestFollow = { timelineId ->
                if (showNoInternetConnection()) return@UsersCirclesAdapter
                viewModel.requestFollowTimeline(timelineId)
            },
            onUnFollow = { timelineId ->
                if (showNoInternetConnection()) return@UsersCirclesAdapter
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
        binding.btnInviteToConnect.apply {
            setIsVisible(!viewModel.isUserMyFollower())
            setOnClickListener {
                binding.lInviteToConnectLoading.visible()
                viewModel.inviteToMySharedCircle()
            }
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
                        withConfirmation(UnIgnoreUser()) { viewModel.unIgnoreUser() }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) {
            binding.toolbar.apply {
                isEnabled = it
                setEnabledChildren(it)
            }
        }
        viewModel.userLiveData.observeData(this) { setupUserInfo(it) }
        viewModel.timelineLiveDataLiveData.observeData(this) {
            usersCirclesAdapter.submitList(it)
        }
        viewModel.requestFollowLiveData.observeResponse(this,
            success = { showSuccess(getString(R.string.request_sent)) })

        viewModel.inviteToConnectLiveData.observeResponse(this,
            success = {
                showSuccess(getString(R.string.request_sent))
                binding.btnInviteToConnect.gone()
                binding.lInviteToConnectLoading.gone()
            })
        viewModel.ignoreUserLiveData.observeResponse(this,
            success = {
                context?.let { showSuccess(it.getString(R.string.user_ignored)) }
                onBackPressed()
            })
        viewModel.unIgnoreUserLiveData.observeResponse(this,
            success = {
                (activity as? AppCompatActivity)?.let {
                    LauncherActivityUtils.clearCacheAndRestart(it)
                }
            })
        viewModel.isUserIgnoredLiveData?.observeData(this) {
            isUserIgnored = it
            setupMenu()
        }
        viewModel.unFollowUserLiveData.observeResponse(this,
            success = { onBackPressed() })
    }

    private fun setupUserInfo(user: User) {
        with(binding) {
            toolbar.title = user.notEmptyDisplayName()
            tvUserId.text = user.userId
            tvUserName.text = user.notEmptyDisplayName()
            ivUser.loadUserProfileIcon(user.avatarUrl, user.userId)
            rvCircles.setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(
                    getString(
                        R.string.not_following_any_circles_format,
                        user.notEmptyDisplayName()
                    )
                )
            })
        }
    }

}