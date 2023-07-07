package org.futo.circles.gallery.feature.gallery.full_screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.Player
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentFullScreenListPreviewBinding
import org.futo.circles.gallery.feature.gallery.full_screen.list.FullScreenGalleryListAdapter
import org.futo.circles.gallery.feature.gallery.grid.GalleryViewModel

@AndroidEntryPoint
class FullScreenListPreviewFragment : Fragment(R.layout.fragment_full_screen_list_preview) {

    private val binding by viewBinding(FragmentFullScreenListPreviewBinding::bind)

    private val viewModel by viewModels<GalleryViewModel>({ requireParentFragment() })

    private val videoPlayer by lazy {
        ExoPlayer.Builder(requireContext()).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
            addListener(object : Player.Listener {
                override fun onIsLoadingChanged(isLoading: Boolean) {
                    super.onIsLoadingChanged(isLoading)
                    binding.vLoading.setIsVisible(isLoading)
                }
            })
        }
    }

    private val listAdapter by lazy {
        FullScreenGalleryListAdapter(videoPlayer, onLoadMore = { viewModel.loadMore() })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    override fun onPause() {
        super.onPause()
        videoPlayer.pause()
    }

    override fun onResume() {
        super.onResume()
        videoPlayer.play()
    }

    override fun onDestroy() {
        videoPlayer.stop()
        videoPlayer.release()
        super.onDestroy()
    }

    private fun setupViews() {
        binding.vpMediaPager.adapter = listAdapter
    }


    private fun setupObservers() {
        viewModel.galleryItemsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }
}