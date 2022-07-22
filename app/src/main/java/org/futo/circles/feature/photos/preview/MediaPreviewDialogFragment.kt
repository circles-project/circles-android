package org.futo.circles.feature.photos.preview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.navArgs
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.databinding.MediaPreviewDialogFragmentBinding
import org.futo.circles.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.extensions.observeData
import org.futo.circles.extensions.showDialog
import org.futo.circles.extensions.showSuccess
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

    private val binding by lazy {
        getBinding() as MediaPreviewDialogFragmentBinding
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.let {
            it.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            it.statusBarColor = Color.BLACK
        }
        setupToolbar()
        setupObservers()
    }

    @SuppressLint("RestrictedApi")
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
}