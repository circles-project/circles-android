package org.futo.circles.gallery.feature.gallery

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.view.menu.MenuBuilder
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentGalleryBinding
import org.futo.circles.gallery.feature.gallery.full_screen.FullScreenPagerFragment
import org.futo.circles.gallery.feature.gallery.grid.GalleryFragment
import org.futo.circles.gallery.model.DeleteGallery


interface GalleryMediaPreviewListener {
    fun onPreviewMedia(itemId: String, position: Int)
}

@AndroidEntryPoint
class GalleryDialogFragment : BaseFullscreenDialogFragment(DialogFragmentGalleryBinding::inflate),
    GalleryMediaPreviewListener {

    private val args: GalleryDialogFragmentArgs by navArgs()

    private val binding by lazy {
        getBinding() as DialogFragmentGalleryBinding
    }

    private val viewModel by viewModels<GalleryDialogFragmentViewModel>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            @Deprecated("Deprecated in Java")
            override fun onBackPressed() {
                handleBackPress()
            }
        }
    }

    private fun handleBackPress() {
        val addedFragment = childFragmentManager.fragments.first { it.isAdded }
        if (addedFragment is GalleryFragment) dismiss()
        else addGalleryFragment()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        addGalleryFragment()
        setupMenu()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.apply {
            setNavigationOnClickListener { handleBackPress() }
            setOnClickListener { binding.toolbar.showOverflowMenu() }
        }
    }

    private fun addGalleryFragment() {
        childFragmentManager.beginTransaction()
            .replace(
                R.id.lContainer,
                GalleryFragment.create(args.roomId, true)
            )
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
        findNavController().navigateSafe(
            GalleryDialogFragmentDirections.toUpdateGalleryDialogFragment(args.roomId)
        )
    }

    override fun onPreviewMedia(itemId: String, position: Int) {
        val transitioningView = view?.findViewById<ImageView>(R.id.ivCover) ?: return
        childFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .addSharedElement(transitioningView, transitioningView.transitionName)
            .replace(R.id.lContainer, FullScreenPagerFragment.create(args.roomId, position))
            .commitAllowingStateLoss()
    }

}