package com.futo.circles.feature.photos.preview

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.view.menu.MenuBuilder
import androidx.navigation.fragment.navArgs
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.databinding.GalleryImageDialogFragmentBinding
import com.futo.circles.extensions.loadInto
import com.futo.circles.extensions.observeData
import com.futo.circles.extensions.showDialog
import com.futo.circles.extensions.showSuccess
import com.futo.circles.feature.timeline.post.share.ShareProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GalleryImageDialogFragment :
    BaseFullscreenDialogFragment(GalleryImageDialogFragmentBinding::inflate) {

    private val args: GalleryImageDialogFragmentArgs by navArgs()
    private val viewModel by viewModel<GalleryImageViewModel> {
        parametersOf(args.roomId, args.eventId)
    }

    private val binding by lazy {
        getBinding() as GalleryImageDialogFragmentBinding
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
                        viewModel.saveImage()
                        true
                    }
                    R.id.share -> {
                        viewModel.shareImage()
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
        viewModel.galleryImageLiveData.observeData(this) {
            it?.loadInto(binding.ivImage)
        }
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.downloadImageLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.image_saved), false) }
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