package org.futo.circles.core.picker

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import org.futo.circles.R
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.picker.MediaPickerHelper.Companion.IS_VIDEO_AVAILABLE
import org.futo.circles.databinding.PickGalleryImageDialogFragmentBinding
import org.futo.circles.feature.photos.PhotosFragment
import org.futo.circles.feature.photos.gallery.GalleryFragment

interface PickGalleryListener {
    fun onGalleryChosen(id: String)
}

interface PickGalleryMediaListener {
    fun onMediaSelected(uri: Uri, mediaType: MediaType)
}

class PickGalleryMediaDialogFragment :
    BaseFullscreenDialogFragment(PickGalleryImageDialogFragmentBinding::inflate),
    PickGalleryListener, PickGalleryMediaListener {

    private val photosRoomsFragment by lazy { PhotosFragment() }

    private val binding by lazy {
        getBinding() as PickGalleryImageDialogFragmentBinding
    }

    private val isVideoAvailable by lazy {
        arguments?.getBoolean(IS_VIDEO_AVAILABLE) ?: false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            override fun onBackPressed() {
                handleBackPress()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { handleBackPress() }
        addGalleriesFragment()
    }

    private fun handleBackPress() {
        if (photosRoomsFragment.isAdded) dismiss()
        else addGalleriesFragment()
    }

    private fun addGalleriesFragment() {
        binding.toolbar.title = getString(R.string.choose_gallery)
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, photosRoomsFragment)
            .commitAllowingStateLoss()
    }

    private fun addPhotosFragment(roomId: String) {
        binding.toolbar.title = getString(R.string.pick_media)
        val fragment = GalleryFragment.create(roomId, isVideoAvailable)
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, fragment)
            .commitAllowingStateLoss()
    }

    override fun onGalleryChosen(id: String) {
        addPhotosFragment(id)
    }

    override fun onMediaSelected(uri: Uri, mediaType: MediaType) {
        setFragmentResult(
            MediaPickerHelper.pickMediaRequestKey,
            bundleOf(
                MediaPickerHelper.uriKey to uri.toString(),
                MediaPickerHelper.mediaTypeKey to mediaType.ordinal
            )
        )
        dismiss()
    }

    companion object {
        fun create(isVideoAvailable: Boolean) = PickGalleryMediaDialogFragment().apply {
            arguments = bundleOf(IS_VIDEO_AVAILABLE to isVideoAvailable)
        }
    }

}