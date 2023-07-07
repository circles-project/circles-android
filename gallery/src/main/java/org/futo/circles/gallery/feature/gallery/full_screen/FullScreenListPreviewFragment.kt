package org.futo.circles.gallery.feature.gallery.full_screen

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeData
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentFullScreenListPreviewBinding
import org.futo.circles.gallery.feature.gallery.grid.GalleryViewModel

@AndroidEntryPoint
class FullScreenListPreviewFragment : Fragment(R.layout.fragment_full_screen_list_preview) {

    private val binding by viewBinding(FragmentFullScreenListPreviewBinding::bind)

    private val viewModel by viewModels<GalleryViewModel>({ requireParentFragment() })

    private val listAdapter by lazy { MediaPagerAdapter(this, arguments?.getString(ROOM_ID) ?: "") }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }


    private fun setupViews() {
        binding.vpMediaPager.adapter = listAdapter
    }

    private fun setupObservers() {
        viewModel.galleryItemsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }

    companion object {
        private const val ROOM_ID = "roomId"
        fun create(roomId: String) = FullScreenListPreviewFragment().apply {
            arguments = bundleOf(ROOM_ID to roomId)
        }
    }
}