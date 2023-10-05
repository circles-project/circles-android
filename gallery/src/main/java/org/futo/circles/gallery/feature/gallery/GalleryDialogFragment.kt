package org.futo.circles.gallery.feature.gallery

import android.app.Dialog
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.NetworkObserver
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setEnabledChildren
import org.futo.circles.core.fragment.BackPressOwner
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentGalleryBinding
import org.futo.circles.gallery.feature.gallery.full_screen.FullScreenPagerFragment
import org.futo.circles.gallery.feature.gallery.grid.GalleryGridFragment


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

    private val galleryFragment by lazy { GalleryGridFragment.create(args.roomId) }

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
            .commit()
    }

    private fun setupObservers() {
        NetworkObserver.observe(this) {
            binding.toolbar.apply {
                isEnabled = it
                setEnabledChildren(it)
            }
        }
        viewModel.titleLiveData?.observeData(this) { title ->
            binding.toolbar.title = title
        }
        viewModel.deleteGalleryLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    private fun setupMenu() {
        with(binding.toolbar) {
            inflateMenu(org.futo.circles.core.R.menu.timeline_menu)
            setupMenuClickListener()
        }
    }

    private fun setupMenuClickListener() {
        binding.toolbar.apply {
            setOnClickListener { navigateToOptions() }
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    org.futo.circles.core.R.id.settings -> navigateToOptions()
                }
                return@setOnMenuItemClickListener true
            }
        }
    }

    private fun navigateToOptions() {
        findNavController().navigateSafe(
            GalleryDialogFragmentDirections.toTimelineOptions(args.roomId, CircleRoomTypeArg.Photo)
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
            .commit()
    }

    override fun onChildBackPress(callback: OnBackPressedCallback) {
        handleBackPress()
    }

}