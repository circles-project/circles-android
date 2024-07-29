package org.futo.circles.feature.home

import android.Manifest
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.base.DeepLinkIntentHandler
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setSupportActionBar
import org.futo.circles.core.feature.picker.helper.RuntimePermissionHelper
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.ResLoadingData
import org.futo.circles.core.model.RoomRequestTypeArg
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.databinding.FragmentBottomNavigationBinding
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.sync.SyncState


@AndroidEntryPoint
class HomeFragment :
    BaseBindingFragment<FragmentBottomNavigationBinding>(FragmentBottomNavigationBinding::inflate),
    DeepLinkIntentHandler {

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermissionHelper =
        RuntimePermissionHelper(this, Manifest.permission.POST_NOTIFICATIONS)

    private val viewModel by viewModels<HomeViewModel>()
    private val validateLoadingDialog by lazy { LoadingDialog(requireContext()) }
    private val syncLoadingDialog by lazy { LoadingDialog(requireContext()) }

    private val roomIdParam = "roomId"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findChildNavController()?.let { controller ->
            binding.bottomNavigationView.setupWithNavController(controller)
            setupToolBar(controller)
        }
        setupObservers()
        registerPushNotifications()
        handleDeepLinks()
    }

    override fun onNewIntent() {
        handleDeepLinks()
    }

    private fun handleDeepLinks() {
        handleOpenFromNotification()
        handleOpenFromShareRoomUrl()
    }

    private fun handleOpenFromNotification() {
        val roomId = activity?.intent?.getStringExtra(roomIdParam) ?: return
        val summary = MatrixSessionProvider.currentSession?.getRoomSummary(roomId) ?: return

        val requestType = if (summary.roomType == GROUP_TYPE) RoomRequestTypeArg.Group
        else if (summary.roomType == TIMELINE_TYPE) RoomRequestTypeArg.Circle
        else if (summary.roomType == null) RoomRequestTypeArg.DM
        else return

        with(binding.bottomNavigationView) {
            post {
                selectedItemId = when (requestType) {
                    RoomRequestTypeArg.DM -> R.id.direct_messages_nav_graph
                    RoomRequestTypeArg.Group -> R.id.groups_nav_graph
                    else -> R.id.circles_nav_graph
                }
            }
        }

        when (summary.membership) {
            Membership.INVITE -> handleInviteNotificationOpen(requestType)
            Membership.JOIN -> handlePostNotificationOpen(summary)
            else -> return
        }
        activity?.intent?.removeExtra(roomIdParam)
    }

    private fun handleInviteNotificationOpen(type: RoomRequestTypeArg) {
        binding.bottomNavigationView.post {
            findNavController().navigateSafe(HomeFragmentDirections.toRoomRequests(type))
        }
    }

    private fun handlePostNotificationOpen(summary: RoomSummary) {
        binding.bottomNavigationView.post {
            findNavController().navigateSafe(HomeFragmentDirections.toTimeline(summary.roomId))
        }
    }

    private fun handleOpenFromShareRoomUrl() {
        val uri = activity?.intent?.data ?: return
        val uriString = uri.toString()
        findNavController().navigateSafe(
            HomeFragmentDirections.toRoomWellKnownDialogFragment(uriString)
        )
        activity?.intent?.data = null
    }

    private fun setupObservers() {
        viewModel.validateWorkspaceResultLiveData.observeResponse(this)
        viewModel.validateWorkspaceLoadingLiveData.observeData(this) {
            validateLoadingDialog.handleLoading(it)
        }
        viewModel.syncStateLiveData.observeData(this) {
            if (it is SyncState.Running && it.afterPause && NetworkObserver.isConnected()) {
                syncLoadingDialog.handleLoading(ResLoadingData(org.futo.circles.auth.R.string.session_sync))
            } else syncLoadingDialog.dismiss()
        }
    }

    private fun registerPushNotifications() {
        if (VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            notificationPermissionHelper.runWithPermission {
                viewModel.registerPushNotifications()
            }
        else viewModel.registerPushNotifications()
    }

    private fun findChildNavController() =
        (childFragmentManager.findFragmentById(R.id.bottom_nav_host_fragment) as? NavHostFragment)?.navController

    private fun setupToolBar(navController: NavController) {
        setSupportActionBar(binding.toolbar)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.circlesFragment,
                R.id.groupsFragment,
                R.id.directMessagesFragment,
                R.id.peopleFragment,
                org.futo.circles.settings.R.id.settingsFragment,
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }


}