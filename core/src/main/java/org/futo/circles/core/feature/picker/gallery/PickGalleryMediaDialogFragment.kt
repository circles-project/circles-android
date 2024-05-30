package org.futo.circles.core.feature.picker.gallery

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.base.fragment.BaseFullscreenDialogFragment
import org.futo.circles.core.databinding.DialogFragmentPickGalleryImageBinding
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.feature.picker.gallery.media.PickMediaItemFragment
import org.futo.circles.core.feature.picker.gallery.rooms.PickGalleryFragment
import org.futo.circles.core.feature.picker.helper.MediaPickerHelper


@AndroidEntryPoint
class PickGalleryMediaDialogFragment :
    BaseFullscreenDialogFragment<DialogFragmentPickGalleryImageBinding>(
        DialogFragmentPickGalleryImageBinding::inflate
    ) {

    private val viewModel by viewModels<PickGalleryMediaViewModel>()
    private val photosRoomsFragment by lazy { PickGalleryFragment() }

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
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.btnAdd.setOnClickListener {
            binding.btnAdd.setIsLoading(true)
            viewModel.picksSelectedItems(requireContext())
        }
    }

    private fun setupObservers() {
        viewModel.selectGalleryEventLiveData.observeData(this) { id ->
            binding.toolbar.title = getString(R.string.pick_media)
            binding.btnAdd.setIsVisible(isMultiselect)
            replaceFragment(PickMediaItemFragment.create(id, isVideoAvailable, isMultiselect))
        }
        viewModel.selectedMediaItemsLiveData.observeData(this) {
            binding.btnAdd.isEnabled = it.isNotEmpty()
        }
        viewModel.mediaChosenEventLiveData.observeData(this) {
            setFragmentResult(
                MediaPickerHelper.pickMediaRequestKey,
                bundleOf(MediaPickerHelper.pickMediaResultDataKey to Gson().toJson(it))
            )
            binding.btnAdd.setIsLoading(false)
            dismiss()
        }
    }

    private fun handleBackPress() {
        if (photosRoomsFragment.isAdded) dismiss()
        else addGalleriesFragment()
    }

    private fun addGalleriesFragment() {
        binding.btnAdd.gone()
        binding.toolbar.title = getString(R.string.choose_gallery)
        replaceFragment(photosRoomsFragment)
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