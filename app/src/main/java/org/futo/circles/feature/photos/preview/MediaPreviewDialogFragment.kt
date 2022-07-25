package org.futo.circles.feature.photos.preview

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.view.isVisible
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.MediaPreviewDialogFragmentBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.timeline.post.share.ShareProvider
import org.futo.circles.model.ImageContent
import org.futo.circles.model.VideoContent
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class MediaPreviewDialogFragment :
    BaseFullscreenDialogFragment(MediaPreviewDialogFragmentBinding::inflate) {

    private val args: MediaPreviewDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<MediaPreviewViewModel> {
        parametersOf(args.roomId, args.eventId)
    }

    private val binding by lazy { getBinding() as MediaPreviewDialogFragmentBinding }

    private val hideHandler = Handler(Looper.myLooper()!!)
    private val showRunnable = Runnable { binding.toolbar.visible() }
    private val hideRunnable = Runnable { hide() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupToolbar()
        setupObservers()
    }

    private fun setupViews() {
        binding.parent.systemUiVisibility =
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        binding.parent.setOnClickListener { toggle() }
        delayedHide()
    }

    private fun setupToolbar() {
        with(binding.toolbar) {
            setNavigationOnClickListener { activity?.onBackPressed() }
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
                        showRemoveConfirmation()
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun setupObservers() {
        viewModel.mediaContentLiveData.observeData(this) { postContent ->
            when (postContent) {
                is ImageContent -> postContent.mediaContentData.loadEncryptedIntoWithAspect(
                    binding.ivImage, postContent.aspectRatio
                )
                is VideoContent -> TODO()
                else -> {}
            }
        }
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.downloadLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.saved), false) }
        }
    }

    private fun showRemoveConfirmation() {
        showDialog(
            titleResIdRes = R.string.remove_image,
            messageResId = R.string.remove_image_message,
            positiveButtonRes = R.string.remove,
            negativeButtonVisible = true,
            positiveAction = {
                viewModel.removeImage()
                activity?.onBackPressed()
            }
        )
    }

    private fun toggle() {
        if (binding.toolbar.isVisible) hide() else show()
    }

    private fun hide() {
        binding.toolbar.gone()
        hideHandler.removeCallbacks(showRunnable)
    }

    private fun show() {
        hideHandler.postDelayed(showRunnable, UI_ANIMATION_DELAY)
        delayedHide()
    }

    private fun delayedHide() {
        hideHandler.removeCallbacks(hideRunnable)
        hideHandler.postDelayed(hideRunnable, AUTO_HIDE_DELAY_MILLIS)
    }

    companion object {
        private const val AUTO_HIDE_DELAY_MILLIS = 3000L
        private const val UI_ANIMATION_DELAY = 300L
    }
}