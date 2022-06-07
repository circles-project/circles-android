package com.futo.circles.feature.photos.save

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.futo.circles.R
import com.futo.circles.core.fragment.BaseFullscreenDialogFragment
import com.futo.circles.core.fragment.HasLoadingState
import com.futo.circles.databinding.SaveToGalleryDialogFragmentBinding
import com.futo.circles.feature.photos.PhotosFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SaveToGalleyDialogFragment :
    BaseFullscreenDialogFragment(SaveToGalleryDialogFragmentBinding::inflate),
    HasLoadingState {

    override val fragment: Fragment = this

    private val viewModel by viewModel<SaveToGalleryViewModel>()

    private val binding by lazy {
        getBinding() as SaveToGalleryDialogFragmentBinding
    }

    private val selectGalleryFragment by lazy { PhotosFragment.create() }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
        addSelectGalleryFragment()
        setupObservers()
        binding.btnSave.setOnClickListener {

            startLoading(binding.btnSave)
        }
    }

    private fun addSelectGalleryFragment() {
        childFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectGalleryFragment)
            .commitAllowingStateLoss()
    }

    private fun setupObservers() {
//        viewModel.inviteResultLiveData.observeResponse(this,
//            success = {
//                showSuccess(getString(R.string.image_saved), true)
//                activity?.onBackPressed()
//            }
//        )
    }
}