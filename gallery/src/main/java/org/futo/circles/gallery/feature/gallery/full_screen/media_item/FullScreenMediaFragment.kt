package org.futo.circles.gallery.feature.gallery.full_screen.media_item

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.loadEncryptedIntoWithAspect
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentFullScreenMediaBinding

@AndroidEntryPoint
class FullScreenMediaFragment : Fragment(R.layout.fragment_full_screen_media) {

    private val binding by viewBinding(FragmentFullScreenMediaBinding::bind)

    private val viewModel by viewModels<FullScreenMediaViewModel>()

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.loadData(requireContext())
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
        binding.videoView.player = videoPlayer
        val transitionName = arguments?.getString(EVENT_ID) ?: ""
        binding.ivImage.transitionName = transitionName
        binding.videoView.transitionName = transitionName
    }

    private fun setupObservers() {
        viewModel.imageLiveData.observeData(this) {
            binding.videoView.apply {
                transitionName = null
                gone()
            }
            it.mediaFileData.loadEncryptedIntoWithAspect(
                binding.ivImage,
                it.aspectRatio,
                it.mediaContentInfo.thumbHash
            )
            binding.ivImage.post { parentFragment?.startPostponedEnterTransition() }
        }
        viewModel.videoLiveData.observeData(this) {
            binding.ivImage.apply {
                transitionName = null
                gone()
            }
            videoPlayer.setMediaItem(MediaItem.fromUri(it.second))
            videoPlayer.prepare()
            videoPlayer.play()
            binding.videoView.post { parentFragment?.startPostponedEnterTransition() }
        }
    }

    companion object {
        private const val ROOM_ID = "roomId"
        private const val EVENT_ID = "eventId"

        fun create(roomId: String, eventId: String) =
            FullScreenMediaFragment().apply {
                arguments = bundleOf(ROOM_ID to roomId, EVENT_ID to eventId)
            }
    }
}