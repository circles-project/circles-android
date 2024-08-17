package org.futo.circles.gallery.feature

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.picker.helper.RuntimePermissionHelper
import org.futo.circles.core.model.GalleryListItem
import org.futo.circles.core.view.EmptyTabPlaceholderView
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentPhotosBinding

@AndroidEntryPoint
class PhotosFragment :
    BaseBindingFragment<FragmentPhotosBinding>(FragmentPhotosBinding::inflate) {

    private val viewModel by viewModels<PhotosViewModel>()
    private val listAdapter by lazy {
        PhotosListAdapter(
            onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) },
            onOpenInvitesClicked = {
                findNavController().navigateSafe(PhotosFragmentDirections.toRoomRequests())
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
            })
            adapter = listAdapter
        }
        binding.ivCreateGallery.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupToolbar() {
        binding.ivMediaBackup.apply {
            setIsVisible(CirclesAppConfig.isMediaBackupEnabled)
            setOnClickListener { openBackupSettingsWithNecessaryPermissions() }
        }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData.observeData(this) { listAdapter.submitList(it) }
    }

    private fun onRoomListItemClicked(room: GalleryListItem) {
        findNavController().navigateSafe(PhotosFragmentDirections.toGalleryFragment(room.id))
    }

    private fun openBackupSettingsWithNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readMediaPermissionHelper.runWithPermission { navigateToBackupSettings() }
        else navigateToBackupSettings()
    }

    private fun navigateToCreateRoom() {
        findNavController().navigateSafe(PhotosFragmentDirections.toCreateGalleryDialogFragment())
    }

    private fun navigateToBackupSettings() {
        findNavController().navigateSafe(PhotosFragmentDirections.toMediaBackupDialogFragment())
    }
}