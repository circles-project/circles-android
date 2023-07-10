package org.futo.circles.gallery.feature.gallery.full_screen

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeData
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentFullScreenPagerBinding
import org.futo.circles.gallery.feature.gallery.grid.GalleryViewModel


@AndroidEntryPoint
class FullScreenPagerFragment : Fragment(R.layout.fragment_full_screen_pager) {

    private val binding by viewBinding(FragmentFullScreenPagerBinding::bind)

    private val viewModel by viewModels<GalleryViewModel>({ requireParentFragment() })

    private val pagerAdapter by lazy {
        MediaPagerAdapter(
            this,
            arguments?.getString(ROOM_ID) ?: ""
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewsWithTransition()
        setupObservers()
    }

    private fun prepareSharedElementTransition() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.image_shared_element_transition)
        postponeEnterTransition()
    }

    private fun setupViewsWithTransition() {
        binding.vpMediaPager.adapter = pagerAdapter
        binding.vpMediaPager.post {
            binding.vpMediaPager.setCurrentItem(arguments?.getInt(POSITION) ?: 0, false)
        }
        prepareSharedElementTransition()
    }

    private fun setupObservers() {
        viewModel.galleryItemsLiveData.observeData(this) {
            pagerAdapter.submitList(it)
        }
    }

    companion object {
        private const val ROOM_ID = "roomId"
        const val POSITION = "position"
        fun create(roomId: String, itemPosition: Int) = FullScreenPagerFragment().apply {
            arguments = bundleOf(ROOM_ID to roomId, POSITION to itemPosition)
        }
    }
}