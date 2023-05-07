package org.futo.circles.feature.photos

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
import androidx.navigation.fragment.findNavController
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.picker.PickGalleryListener
import org.futo.circles.core.picker.RuntimePermissionHelper
import org.futo.circles.databinding.FragmentRoomsBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.photos.list.PhotosListAdapter
import org.futo.circles.model.GalleryListItem
import org.koin.androidx.viewmodel.ext.android.viewModel

class PhotosFragment : Fragment(R.layout.fragment_rooms), MenuProvider {

    private val viewModel by viewModel<PhotosViewModel>()
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
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.photos_tab_menu, menu)
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
            findNavController().navigate(PhotosFragmentDirections.toGalleryFragment(room.id))
        }
    }

    private fun openBackupSettingsWithNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            readMediaPermissionHelper.runWithPermission { navigateToBackupSettings() }
        else navigateToBackupSettings()
    }

    private fun navigateToCreateRoom() {
        findNavController().navigate(PhotosFragmentDirections.toCreateGalleryDialogFragment())
    }

    private fun navigateToBackupSettings() {
        findNavController().navigate(PhotosFragmentDirections.toMediaBackupDialogFragment())
    }
}