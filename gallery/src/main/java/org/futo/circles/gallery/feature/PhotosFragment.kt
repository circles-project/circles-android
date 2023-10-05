package org.futo.circles.gallery.feature

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.CirclesAppConfig
import org.futo.circles.core.NetworkObserver
import org.futo.circles.core.databinding.FragmentRoomsBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.setEnabledViews
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.RequestGalleryListItem
import org.futo.circles.core.picker.helper.RuntimePermissionHelper
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.gallery.R

@AndroidEntryPoint
class PhotosFragment : Fragment(org.futo.circles.core.R.layout.fragment_rooms), MenuProvider {

    private val viewModel by viewModels<PhotosViewModel>()
    private val binding by viewBinding(FragmentRoomsBinding::bind)

    private val listAdapter by lazy {
        PhotosListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onInviteClicked = { roomListItem, isAccepted ->
                onInviteClicked(roomListItem, isAccepted)
            },
            onRequestClicked = { roomListItem, isAccepted ->
                onRequestClicked(roomListItem, isAccepted)
            })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val readMediaPermissionHelper =
        RuntimePermissionHelper(this, Manifest.permission.READ_MEDIA_IMAGES)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        activity?.addMenuProvider(this, viewLifecycleOwner)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateMenu(menu: Menu, inflater: MenuInflater) {
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.photos_tab_menu, menu)
        menu.findItem(R.id.backup).isVisible = CirclesAppConfig.isMediaBackupEnabled
    }


    override fun onMenuItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.backup -> openBackupSettingsWithNecessaryPermissions()
        }
        return true
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.photos_empty_message))
            })
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) { setEnabledViews(it) }
        viewModel.roomsLiveData.observeData(this) { listAdapter.submitList(it) }
        viewModel.inviteResultLiveData.observeResponse(this)
    }

    private fun onRoomListItemClicked(room: GalleryListItem) {
        findNavController().navigateSafe(PhotosFragmentDirections.toGalleryFragment(room.id))
    }

    private fun openBackupSettingsWithNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readMediaPermissionHelper.runWithPermission { navigateToBackupSettings() }
        else navigateToBackupSettings()
    }

    private fun onInviteClicked(room: GalleryListItem, isAccepted: Boolean) {
        if (isAccepted) viewModel.acceptPhotosInvite(room.id)
        else viewModel.rejectInvite(room.id)
    }

    private fun onRequestClicked(room: RequestGalleryListItem, isAccepted: Boolean) {
        if (isAccepted) viewModel.inviteUser(room)
        else viewModel.kickUser(room)
    }

    private fun navigateToCreateRoom() {
        findNavController().navigateSafe(PhotosFragmentDirections.toCreateGalleryDialogFragment())
    }

    private fun navigateToBackupSettings() {
        findNavController().navigateSafe(PhotosFragmentDirections.toMediaBackupDialogFragment())
    }
}