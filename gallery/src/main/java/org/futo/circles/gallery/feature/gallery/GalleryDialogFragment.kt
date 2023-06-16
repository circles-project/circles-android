package org.futo.circles.gallery.feature.gallery

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setToolbarTitle
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentGalleryBinding
import org.futo.circles.gallery.model.DeleteGallery

interface GalleryMediaPreviewListener {
    fun onPreviewMedia(itemId: String)
}

@AndroidEntryPoint
class GalleryDialogFragment : BaseFullscreenDialogFragment(DialogFragmentGalleryBinding::inflate),
    GalleryMediaPreviewListener {

    private val args: GalleryDialogFragmentArgs by navArgs()

    private val binding by lazy {
        getBinding() as DialogFragmentGalleryBinding
    }

    private val viewModel by viewModels<GalleryDialogFragmentViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addGalleryFragment()
        setupMenu()
        setupObservers()
    }

    private fun addGalleryFragment() {
        val fragment = GalleryFragment.create(args.roomId, true)
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, fragment)
            .commitAllowingStateLoss()
    }

    private fun setupObservers() {
        viewModel.titleLiveData?.observeData(this) { title ->
            binding.toolbar.title = title
        }
        viewModel.deleteGalleryLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    @SuppressLint("RestrictedApi")
    private fun setupMenu() {
        with(binding.toolbar) {
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            inflateMenu(R.menu.gallery_timeline_menu)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.configureGallery -> navigateToUpdateRoom()
                    R.id.deleteGallery -> withConfirmation(DeleteGallery()) { viewModel.deleteGallery() }
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun navigateToUpdateRoom() {
        findNavController().navigate(
            GalleryDialogFragmentDirections.toUpdateGalleryDialogFragment(args.roomId)
        )
    }

    override fun onPreviewMedia(itemId: String) {
        findNavController().navigate(
            GalleryDialogFragmentDirections.toGalleryImageDialogFragment(args.roomId, itemId)
        )
    }

}