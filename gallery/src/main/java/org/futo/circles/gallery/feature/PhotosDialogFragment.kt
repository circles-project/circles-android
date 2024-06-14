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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.FragmentRoomsBinding
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.picker.helper.RuntimePermissionHelper
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.model.RemoveImage
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentPhotosBinding

@AndroidEntryPoint
class PhotosDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentPhotosBinding>(DialogFragmentPhotosBinding::inflate){

    private val viewModel by viewModels<PhotosViewModel>()
    private val listAdapter by lazy {
        PhotosListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onOpenInvitesClicked = {
                findNavController().navigateSafe(PhotosDialogFragmentDirections.toRoomRequests())
            }
        )
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val readMediaPermissionHelper =
        RuntimePermissionHelper(this, Manifest.permission.READ_MEDIA_IMAGES)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        setupToolbar()
    }

    private fun setupViews() {
        binding.rvRooms.apply {
            setEmptyView(EmptyTabPlaceholderView(requireContext()).apply {
                setText(getString(R.string.photos_empty_message))
                setArrowVisible(true)
            })
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    @SuppressLint("RestrictedApi")
    private fun setupToolbar() {
        with(binding.toolbar) {
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            menu.findItem(R.id.backup).isVisible = CirclesAppConfig.isMediaBackupEnabled
            setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when (item.itemId) {
                    R.id.backup -> {
                        openBackupSettingsWithNecessaryPermissions()
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) { listAdapter.submitList(it) }
    }

    private fun onRoomListItemClicked(room: GalleryListItem) {
        findNavController().navigateSafe(PhotosDialogFragmentDirections.toGalleryFragment(room.id))
    }

    private fun openBackupSettingsWithNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readMediaPermissionHelper.runWithPermission { navigateToBackupSettings() }
        else navigateToBackupSettings()
    }

    private fun navigateToCreateRoom() {
        findNavController().navigateSafe(PhotosDialogFragmentDirections.toCreateGalleryDialogFragment())
    }

    private fun navigateToBackupSettings() {
        findNavController().navigateSafe(PhotosDialogFragmentDirections.toMediaBackupDialogFragment())
    }
}