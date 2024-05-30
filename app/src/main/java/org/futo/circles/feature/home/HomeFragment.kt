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
import org.futo.circles.auth.feature.workspace.WorkspaceDialogFragment
import org.futo.circles.core.base.DeepLinkIntentHandler
import org.futo.circles.core.base.NetworkObserver
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setSupportActionBar
import org.futo.circles.core.feature.picker.helper.RuntimePermissionHelper
import org.futo.circles.core.model.GROUP_TYPE
import org.futo.circles.core.model.InviteTypeArg
import org.futo.circles.core.model.LoadingData
import org.futo.circles.core.model.TIMELINE_TYPE
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.core.view.LoadingDialog
import org.futo.circles.databinding.FragmentBottomNavigationBinding
import org.futo.circles.gallery.feature.backup.service.MediaBackupServiceManager
import org.matrix.android.sdk.api.session.getRoomSummary
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.model.RoomSummary
import org.matrix.android.sdk.api.session.sync.SyncState
import javax.inject.Inject


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

    @Inject
    lateinit var mediaBackupServiceManager: MediaBackupServiceManager

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

    override fun onStop() {
        super.onStop()
        mediaBackupServiceManager.unbindMediaService(requireContext())
    }

    private fun handleOpenFromNotification() {
        val roomId = activity?.intent?.getStringExtra(roomIdParam) ?: return
        val summary = MatrixSessionProvider.currentSession?.getRoomSummary(roomId) ?: return
        val type = summary.roomType?.takeIf { it == GROUP_TYPE || it == TIMELINE_TYPE } ?: return

        with(binding.bottomNavigationView) {
            post {
                selectedItemId = if (type == GROUP_TYPE) R.id.groups_nav_graph
                else R.id.circles_nav_graph
            }
        }

        when (summary.membership) {
            Membership.INVITE -> handleInviteNotificationOpen(type)
            Membership.JOIN -> handlePostNotificationOpen(type, summary)
            else -> return
        }
        activity?.intent?.removeExtra(roomIdParam)
    }

    private fun handleInviteNotificationOpen(type: String) {
        val inviteType = if (type == GROUP_TYPE) InviteTypeArg.Group
        else InviteTypeArg.Circle
        binding.bottomNavigationView.post {
            findNavController().navigateSafe(HomeFragmentDirections.toInvites(inviteType))
        }
    }

    private fun handlePostNotificationOpen(type: String, summary: RoomSummary) {
        val roomId = viewModel.getNotificationGroupOrCircleId(summary) ?: return
        val timelineId = if (type == GROUP_TYPE) null else getTimelineRoomFor(roomId)?.roomId
        binding.bottomNavigationView.post {
            findNavController().navigateSafe(
                HomeFragmentDirections.toTimeline(roomId, timelineId)
            )
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
        viewModel.mediaBackupSettingsLiveData?.observeData(this) {
            mediaBackupServiceManager.bindMediaServiceIfNeeded(requireContext(), it)
        }
        viewModel.validateWorkspaceResultLiveData.observeResponse(this,
            error = { WorkspaceDialogFragment().show(childFragmentManager, "workspace") },
            onRequestInvoked = { validateLoadingDialog.dismiss() })
        viewModel.validateWorkspaceLoadingLiveData.observeData(this) {
            validateLoadingDialog.handleLoading(it)
        }
        viewModel.syncStateLiveData.observeData(this) {
            if (it is SyncState.Running && it.afterPause && NetworkObserver.isConnected()) {
                syncLoadingDialog.handleLoading(LoadingData(org.futo.circles.auth.R.string.session_sync))
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
                org.futo.circles.settings.R.id.settingsFragment,
                R.id.circlesFragment,
                R.id.peopleFragment,
                R.id.groupsFragment,
                org.futo.circles.gallery.R.id.photosFragment
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }


}