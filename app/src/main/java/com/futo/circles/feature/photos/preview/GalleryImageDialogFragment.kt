package com.futo.circles.feature.photos.preview

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
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
        setHasOptionsMenu(true)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    private fun setupObservers() {
        viewModel.galleryImageLiveData.observeData(this) {
            it?.loadInto(binding.ivImage)
        }
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.downloadImageLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.image_saved), true) }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.gallery_image_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save -> {
                viewModel.saveImage()
                return true
            }
            R.id.share -> {
                viewModel.shareImage()
                return true
            }
            R.id.delete -> {
                showRemoveConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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