package org.futo.circles.core.picker.helper

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.picker.gallery.PickGalleryMediaDialogFragment

class AllMediaPickerHelper(
    private val fragment: Fragment,
    private val allMediaTypeAvailable: Boolean = false
) : DeviceMediaPickerHelper(fragment, allMediaTypeAvailable) {

    override val isGalleryAvailable: Boolean = true

    override fun onGalleryMethodSelected() {
        showGalleryPicker()
    }

    private fun showGalleryPicker() {
        fragment.childFragmentManager.setFragmentResultListener(
            pickMediaRequestKey,
            fragment
        ) { key, bundle -> handlePickerFragmentResult(key, bundle) }

        PickGalleryMediaDialogFragment.create(allMediaTypeAvailable)
            .show(fragment.childFragmentManager, "PickGalleryMediaDialogFragment")
    }

    private fun handlePickerFragmentResult(key: String, bundle: Bundle) {
        if (key == pickMediaRequestKey) {
            val uri = Uri.parse(bundle.getString(uriKey))
            val typeOrdinal = bundle.getInt(mediaTypeKey)
            when (MediaType.values()[typeOrdinal]) {
                MediaType.Image -> onImageSelected?.invoke(itemId, uri)
                MediaType.Video -> onVideoSelected?.invoke(uri)
            }
        }
    }

}