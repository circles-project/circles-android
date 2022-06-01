package com.futo.circles.feature.photos.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.futo.circles.R
import com.futo.circles.databinding.GalleryFragmentBinding
import com.futo.circles.extensions.showDialog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GalleryFragment : Fragment(R.layout.gallery_fragment) {

    private val args: GalleryFragmentArgs by navArgs()
    private val viewModel by viewModel<GalleryViewModel> { parametersOf(args.roomId) }

    private val binding by viewBinding(GalleryFragmentBinding::bind)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupViews()
        setupObservers()
    }

    private fun setupObservers() {

    }

    private fun setupViews() {

    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.gallery_timeline_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureGallery -> {
                navigateToUpdateRoom()
                return true
            }
            R.id.deleteGallery -> {
                showDeleteConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToUpdateRoom() {
        findNavController().navigate(
            GalleryFragmentDirections.toUpdateRoomDialogFragment(args.roomId)
        )
    }

    private fun showDeleteConfirmation() {
        showDialog(
            titleResIdRes = R.string.delete_gallery,
            messageResId = R.string.delete_gallery_message,
            positiveButtonRes = R.string.delete,
            negativeButtonVisible = true,
            positiveAction = { }
        )
    }
}
