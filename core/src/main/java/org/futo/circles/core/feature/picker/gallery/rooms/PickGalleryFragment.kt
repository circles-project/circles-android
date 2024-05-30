package org.futo.circles.core.feature.picker.gallery.rooms

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.base.fragment.BaseBindingFragment
import org.futo.circles.core.databinding.FragmentPickGalleryBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.feature.picker.gallery.PickGalleryMediaViewModel
import org.futo.circles.core.feature.picker.gallery.rooms.list.PickGalleryListAdapter

@AndroidEntryPoint
class PickGalleryFragment : BaseBindingFragment(FragmentPickGalleryBinding::inflate) {

    private val viewModel by viewModels<PickGalleryViewModel>()
    private val parentViewModel by viewModels<PickGalleryMediaViewModel>({ requireParentFragment() })
    private val binding by lazy {
        getBinding() as FragmentPickGalleryBinding
    }
    private val listAdapter by lazy {
        PickGalleryListAdapter(onRoomClicked = { gallery ->
            parentViewModel.onGalleryChosen(gallery.id)
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvRooms.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.galleriesLiveData.observeData(this) { listAdapter.submitList(it) }
    }
}