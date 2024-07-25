package org.futo.circles.core.feature.user

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.DividerItemDecoration
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.base.fragment.HasLoadingState
import org.futo.circles.core.databinding.DialogFragmentUserBinding
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadUserProfileIcon
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.notEmptyDisplayName
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showNoInternetConnection
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.user.list.UsersCirclesAdapter
import org.futo.circles.core.model.DmConnected
import org.futo.circles.core.model.DmHasInvite
import org.futo.circles.core.model.DmInviteSent
import org.futo.circles.core.model.DmNotFound
import org.futo.circles.core.model.IgnoreUser
import org.futo.circles.core.model.UnIgnoreUser
import org.futo.circles.core.model.UnfollowTimeline
import org.futo.circles.core.utils.LauncherActivityUtils
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.matrix.android.sdk.api.session.user.model.User

@AndroidEntryPoint
class UserDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentUserBinding>(DialogFragmentUserBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this
    private val args: UserDialogFragmentArgs by navArgs()
    private val viewModel by viewModels<UserViewModel>()

    private val usersCirclesAdapter by lazy {
        UsersCirclesAdapter(
            onUnFollow = { timelineId ->
                if (showNoInternetConnection()) return@UsersCirclesAdapter
                withConfirmation(UnfollowTimeline()) {
                    viewModel.unFollowTimeline(timelineId)
                }
            },
            onUserClicked = {
                findNavController().navigateSafe(UserDialogFragmentDirections.toUserFragment(it))
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
        with(binding) {
            rvCircles.apply {
                addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                adapter = usersCirclesAdapter
            }
            btnInviteToFollowMe.setOnClickListener {
                findNavController().navigateSafe(
                    UserDialogFragmentDirections.toInviteToFollowMeDialogFragment(args.userId)
                )
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupMenu() {
        with(binding.toolbar) {
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            menu.findItem(R.id.ignore).isVisible = !isUserIgnored
            menu.findItem(R.id.unIgnore).isVisible = isUserIgnored
            setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when (item.itemId) {
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
        viewModel.userLiveData.observeData(this) { setupUserInfo(it) }
        viewModel.usersTimelinesLiveData.observeData(this) {
            usersCirclesAdapter.submitList(it)
        }
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
        viewModel.inviteForDirectMessagesLiveData.observeResponse(
            this,
            success = {
                context?.let { showSuccess(it.getString(R.string.invitation_sent)) }
            })
        viewModel.acceptDmInviteLiveData.observeResponse(this)
        viewModel.dmRoomStateLiveData.observeData(this) { state ->
            stopLoading()
            when (state) {
                is DmConnected -> setupHasDirectMessages(state.roomId)
                is DmHasInvite -> setupHasInviteForDirectMessages(state.roomId)
                DmInviteSent -> setupDMInvitationSent()
                DmNotFound -> setupNoDirectMessages()
            }
        }
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

    private fun setupDMInvitationSent() {
        binding.tvDmInvitationSent.visible()
        binding.btnDirectMessages.gone()
    }

    private fun setupNoDirectMessages() {
        binding.btnDirectMessages.apply {
            visible()
            setText(getString(R.string.invite_for_direct_messages))
            setOnClickListener {
                startLoading(binding.btnDirectMessages)
                viewModel.inviteForDirectMessages()
            }
        }
    }

    private fun setupHasDirectMessages(roomId: String) {
        binding.btnDirectMessages.apply {
            visible()
            setText(getString(R.string.open_direct_messages))
            setOnClickListener {
                findNavController().navigateSafe(
                    Uri.parse("circles://app/dmTimeline/$roomId")
                )
            }
        }
    }

    private fun setupHasInviteForDirectMessages(roomId: String) {
        binding.btnDirectMessages.apply {
            visible()
            setText(getString(R.string.accept_direct_messages_invite))
            setOnClickListener {
                startLoading(binding.btnDirectMessages)
                viewModel.acceptDmInvite(roomId)
            }
        }
    }

}