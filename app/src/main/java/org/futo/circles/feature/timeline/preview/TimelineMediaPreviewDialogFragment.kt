package org.futo.circles.feature.timeline.preview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.feature.media.FullScreenMediaFragment
import org.futo.circles.core.feature.share.ShareProvider
import org.futo.circles.core.model.RemoveImage
import org.futo.circles.databinding.DialogFragmentTimelineMediaPreviewBinding
import org.futo.circles.gallery.R

@AndroidEntryPoint
class TimelineMediaPreviewDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentTimelineMediaPreviewBinding>(
        DialogFragmentTimelineMediaPreviewBinding::inflate
    ) {

    private val viewModel by viewModels<TimelineMediaPreviewViewModel>()
    private val args: TimelineMediaPreviewDialogFragmentArgs by navArgs()

    private val mediaFragment by lazy { FullScreenMediaFragment.create(args.roomId, args.eventId) }

    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let {
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.statusBarColor = Color.BLACK
        }
        setupViews()
        setupToolbar()
        setupObservers()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupViews() {
        replaceFragment(mediaFragment)
        binding.lContainer.setOnClickListener { binding.toolbar.setIsVisible(binding.toolbar.isVisible.not()) }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, fragment)
            .commitAllowingStateLoss()
    }

    @SuppressLint("RestrictedApi")
    private fun setupToolbar() {
        with(binding.toolbar) {
            setNavigationOnClickListener { onBackPressed() }
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when (item.itemId) {
                    R.id.save -> {
                        viewModel.save()
                        true
                    }

                    R.id.share -> {
                        viewModel.share()
                        true
                    }

                    R.id.delete -> {
                        withConfirmation(RemoveImage()) {
                            viewModel.removeImage()
                            onBackPressed()
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.downloadLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.saved)) }
        }
    }
}