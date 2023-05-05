package org.futo.circles.feature.home

import android.Manifest
import android.os.Build
import android.os.Build.VERSION
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.MainActivity
import org.futo.circles.R
import org.futo.circles.core.picker.RuntimePermissionHelper
import org.futo.circles.databinding.FragmentBottomNavigationBinding
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.setSupportActionBar
import org.futo.circles.feature.photos.backup.service.MediaBackupServiceManager
import org.futo.circles.model.GROUP_TYPE
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.activityViewModel
import org.matrix.android.sdk.api.session.getRoomSummary

class HomeFragment : Fragment(R.layout.fragment_bottom_navigation) {

    private val binding by viewBinding(FragmentBottomNavigationBinding::bind)

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val notificationPermissionHelper =
        RuntimePermissionHelper(this, Manifest.permission.POST_NOTIFICATIONS)

    private val viewModel by activityViewModel<HomeViewModel>()
    private val systemNoticesCountViewModel by activityViewModel<SystemNoticesCountSharedViewModel>()
    private val mediaBackupServiceManager: MediaBackupServiceManager by inject()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findChildNavController()?.let { controller ->
            binding.bottomNavigationView.setupWithNavController(controller)
            setupToolBar(controller)
        }
        setupObservers()
        registerPushNotifications()
        handleOpenFromNotification()
    }

    private fun handleOpenFromNotification() {
        val roomId = activity?.intent?.getStringExtra(MainActivity.ROOM_ID_PARAM) ?: return
        val summary = MatrixSessionProvider.currentSession?.getRoomSummary(roomId) ?: return
        binding.bottomNavigationView.selectedItemId =
            if (summary.roomType == GROUP_TYPE) R.id.groups_nav_graph
            else R.id.circles_nav_graph
        viewModel.postNotificationData(summary)
        activity?.intent?.removeExtra(MainActivity.ROOM_ID_PARAM)
    }

    private fun setupObservers() {
        systemNoticesCountViewModel.systemNoticesCountLiveData?.observeData(this) {
            val count = it ?: 0
            binding.bottomNavigationView.getOrCreateBadge(R.id.settings_nav_graph).apply {
                isVisible = count > 0
                number = count
            }
        }
        viewModel.inviteIntoSharedSpaceLiveData?.observeData(this) {
            viewModel.autoAcceptInviteOnKnock(it)
        }
        viewModel.mediaBackupSettingsLiveData?.observeData(this) {
            mediaBackupServiceManager.bindMediaServiceIfNeeded(requireContext(), it)
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
                R.id.settingsFragment,
                R.id.circlesFragment,
                R.id.peopleFragment,
                R.id.groupsFragment,
                R.id.photosFragment
            )
        )
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
    }
}