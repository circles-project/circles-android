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
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.badge.BadgeUtils
import com.google.android.material.badge.ExperimentalBadgeUtils
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BackPressOwner
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.navigateSafe
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.DialogFragmentGalleryBinding
import org.futo.circles.gallery.feature.gallery.full_screen.FullScreenPagerFragment
import org.futo.circles.gallery.feature.gallery.grid.GalleryGridFragment


interface GalleryMediaPreviewListener {
    fun onPreviewMedia(itemId: String, view: View, position: Int)
}

@ExperimentalBadgeUtils
@AndroidEntryPoint
class GalleryDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentGalleryBinding>(DialogFragmentGalleryBinding::inflate),
    GalleryMediaPreviewListener, BackPressOwner {

    private val args: GalleryDialogFragmentArgs by navArgs()

    private val galleryFragment by lazy { GalleryGridFragment.create(args.roomId) }

    private val viewModel by viewModels<GalleryDialogFragmentViewModel>()

    private val knocksCountBadgeDrawable by lazy {
        BadgeDrawable.create(requireContext()).apply {
            isVisible = false
            backgroundColor =
                ContextCompat.getColor(requireContext(), android.R.color.holo_red_dark)
        }
    }

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
        if (savedInstanceState == null) addGalleryFragment()
        setupViews()
        setupMenu()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.apply {
            setNavigationOnClickListener { handleBackPress() }
            setOnClickListener { binding.toolbar.showOverflowMenu() }
        }

        BadgeUtils.attachBadgeDrawable(
            knocksCountBadgeDrawable, binding.toolbar,
            org.futo.circles.core.R.id.settings
        )
    }

    private fun addGalleryFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, galleryFragment)
            .commit()
    }

    private fun setupObservers() {
        viewModel.titleLiveData?.observeData(this) { title ->
            binding.toolbar.title = title
        }
        viewModel.deleteGalleryLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
        viewModel.knockRequestCountLiveData.observeData(this) {
            knocksCountBadgeDrawable.apply {
                number = it
                isVisible = it > 0
            }
        }
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
            GalleryDialogFragmentDirections.toTimelineOptions(args.roomId)
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