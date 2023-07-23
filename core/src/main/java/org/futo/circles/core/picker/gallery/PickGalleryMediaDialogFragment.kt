package org.futo.circles.core.picker.gallery

import android.app.Dialog
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.databinding.DialogFragmentPickGalleryImageBinding
import org.futo.circles.core.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.picker.gallery.media.PickMediaItemFragment
import org.futo.circles.core.picker.gallery.rooms.PickGalleryFragment
import org.futo.circles.core.picker.helper.MediaPickerHelper

interface PickGalleryListener {
    fun onGalleryChosen(id: String)
}

interface PickGalleryMediaListener {
    fun onMediaSelected(uri: Uri, mediaType: MediaType)
}

@AndroidEntryPoint
class PickGalleryMediaDialogFragment :
    BaseFullscreenDialogFragment(DialogFragmentPickGalleryImageBinding::inflate),
    PickGalleryListener, PickGalleryMediaListener {

    private val photosRoomsFragment by lazy { PickGalleryFragment() }

    private val binding by lazy {
        getBinding() as DialogFragmentPickGalleryImageBinding
    }

    private val isVideoAvailable by lazy {
        arguments?.getBoolean(IS_VIDEO_AVAILABLE) ?: false
    }

    private val isMultiselect by lazy {
        arguments?.getBoolean(IS_MULTI_SELECT) ?: false
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return object : Dialog(requireActivity(), theme) {
            @Deprecated("Deprecated in Java")
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
        replaceFragment(photosRoomsFragment)
    }

    override fun onGalleryChosen(id: String) {
        binding.toolbar.title = getString(R.string.pick_media)
        replaceFragment(PickMediaItemFragment.create(id, isVideoAvailable, isMultiselect))
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

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, fragment)
            .commitAllowingStateLoss()
    }

    companion object {
        const val IS_VIDEO_AVAILABLE = "IsVideoAvailable"
        const val IS_MULTI_SELECT = "IsMultiSelect"
        fun create(isVideoAvailable: Boolean, isMultiSelect: Boolean) =
            PickGalleryMediaDialogFragment().apply {
                arguments = bundleOf(
                    IS_VIDEO_AVAILABLE to isVideoAvailable,
                    IS_MULTI_SELECT to isMultiSelect
                )
            }
    }

}