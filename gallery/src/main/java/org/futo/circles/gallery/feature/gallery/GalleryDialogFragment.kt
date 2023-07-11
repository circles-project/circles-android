package org.futo.circles.gallery.feature.gallery

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.BackPressOwner
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentGalleryBinding
import org.futo.circles.gallery.feature.gallery.full_screen.FullScreenPagerFragment
import org.futo.circles.gallery.feature.gallery.grid.GalleryGridFragment
import org.futo.circles.gallery.model.DeleteGallery
import org.matrix.android.sdk.api.extensions.tryOrNull


interface GalleryMediaPreviewListener {
    fun onPreviewMedia(itemId: String, view: View, position: Int)
}

@AndroidEntryPoint
class GalleryDialogFragment : BaseFullscreenDialogFragment(DialogFragmentGalleryBinding::inflate),
    GalleryMediaPreviewListener, BackPressOwner {

    private val args: GalleryDialogFragmentArgs by navArgs()

    private val binding by lazy {
        getBinding() as DialogFragmentGalleryBinding
    }

    private val galleryFragment by lazy { GalleryGridFragment.create(args.roomId, true) }

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
        if (addedFragment is GalleryGridFragment) dismiss()
        else {
            setFullScreenMode(false)
            childFragmentManager.popBackStack()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addGalleryFragment()
        setupViews()
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
            .replace(R.id.lContainer, galleryFragment)
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

    private fun setFullScreenMode(isFullScreen: Boolean) {
        dialog?.window?.let {
            it.statusBarColor = if (isFullScreen) Color.BLACK else ContextCompat.getColor(
                requireContext(),
                org.futo.circles.core.R.color.status_bar_color
            )
        }
    }

    override fun onPreviewMedia(itemId: String, view: View, position: Int) {
        setFullScreenMode(true)
        val fragment = FullScreenPagerFragment.create(args.roomId, position)
        childFragmentManager
            .beginTransaction()
            .setReorderingAllowed(true)
            .addSharedElement(view, view.transitionName)
            .replace(R.id.lContainer, fragment, fragment.javaClass.name)
            .addToBackStack(fragment.javaClass.name)
            .commitAllowingStateLoss()
    }

    override fun onChildBackPress(callback: OnBackPressedCallback) {
        handleBackPress()
    }

}