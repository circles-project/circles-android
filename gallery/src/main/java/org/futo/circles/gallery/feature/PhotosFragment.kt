package org.futo.circles.gallery.feature

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
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
import org.futo.circles.core.databinding.FragmentRoomsBinding
import org.futo.circles.core.extensions.bindToFab
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.picker.RuntimePermissionHelper
import org.futo.circles.gallery.R
import org.futo.circles.gallery.feature.list.PhotosListAdapter
import org.futo.circles.gallery.feature.pick.PickGalleryListener
import org.futo.circles.gallery.model.GalleryListItem

@AndroidEntryPoint
class PhotosFragment : Fragment(org.futo.circles.core.R.layout.fragment_rooms), MenuProvider {

    private val viewModel by viewModels<PhotosViewModel>()
    private val binding by viewBinding(FragmentRoomsBinding::bind)

    private var pickGalleryListener: PickGalleryListener? = null
    private val listAdapter by lazy {
        PhotosListAdapter(onRoomClicked = { roomListItem -> onRoomListItemClicked(roomListItem) })
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val readMediaPermissionHelper =
        RuntimePermissionHelper(this, Manifest.permission.READ_MEDIA_IMAGES)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickGalleryListener = parentFragment as? PickGalleryListener
    }

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
        binding.fbAddRoom.setIsVisible(pickGalleryListener == null)
        binding.rvRooms.apply {
            adapter = listAdapter
            bindToFab(binding.fbAddRoom)
        }
        binding.fbAddRoom.setOnClickListener { navigateToCreateRoom() }
    }

    private fun setupObservers() {
        viewModel.roomsLiveData?.observeData(this) { listAdapter.submitList(it) }
    }

    private fun onRoomListItemClicked(room: GalleryListItem) {
        pickGalleryListener?.onGalleryChosen(room.id) ?: run {
            findNavController().navigateSafe(PhotosFragmentDirections.toGalleryFragment(room.id))
        }
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