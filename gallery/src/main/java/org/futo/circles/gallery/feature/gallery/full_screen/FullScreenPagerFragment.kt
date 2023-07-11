package org.futo.circles.gallery.feature.gallery.full_screen

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.app.SharedElementCallback
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.transition.TransitionInflater
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.onBackPressed
import org.futo.circles.core.extensions.showSuccess
import org.futo.circles.core.extensions.withConfirmation
import org.futo.circles.core.fragment.ParentBackPressOwnerFragment
import org.futo.circles.core.share.ShareProvider
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentFullScreenPagerBinding
import org.futo.circles.gallery.feature.gallery.grid.GalleryViewModel
import org.futo.circles.gallery.model.RemoveImage


@AndroidEntryPoint
class FullScreenPagerFragment : ParentBackPressOwnerFragment(R.layout.fragment_full_screen_pager) {

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
        setupToolbar()
        setupObservers()
    }

    private fun prepareSharedElementTransition() {
        sharedElementEnterTransition = TransitionInflater.from(requireContext())
            .inflateTransition(R.transition.image_shared_element_transition)
        setEnterSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String?>,
                    sharedElements: MutableMap<String?, View?>
                ) {
                    val view = getCurrentSelectedFragment()?.view ?: return
                    val image = view.findViewById<View>(R.id.ivImage)
                    val video = view.findViewById<View>(R.id.videoView)
                    sharedElements[names[0]] = if (image.isVisible) image else video
                }
            })
        postponeEnterTransition()
    }

    private fun setupViewsWithTransition() {
        binding.vpMediaPager.apply {
            adapter = pagerAdapter
            registerOnPageChangeCallback(object : OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    setResult(position)
                }
            })
        }
        binding.vpMediaPager.post {
            binding.vpMediaPager.setCurrentItem(arguments?.getInt(POSITION) ?: 0, false)
        }
        prepareSharedElementTransition()
    }

    private fun setupObservers() {
        viewModel.galleryItemsLiveData.observeData(this) {
            pagerAdapter.submitList(it)
        }
        viewModel.shareLiveData.observeData(this) { content ->
            context?.let { ShareProvider.share(it, content) }
        }
        viewModel.downloadLiveData.observeData(this) {
            context?.let { showSuccess(it.getString(R.string.saved)) }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun setupToolbar() {
        with(binding.toolbar) {
            setNavigationOnClickListener { onBackPressed() }
            (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
            setOnMenuItemClickListener { item ->
                return@setOnMenuItemClickListener when (item.itemId) {
                    R.id.save -> {
                        viewModel.save(binding.vpMediaPager.currentItem)
                        true
                    }

                    R.id.share -> {
                        viewModel.share(binding.vpMediaPager.currentItem)
                        true
                    }

                    R.id.delete -> {
                        withConfirmation(RemoveImage()) {
                            viewModel.removeImage(binding.vpMediaPager.currentItem)
                            onBackPressed()
                        }
                        true
                    }

                    else -> false
                }
            }
        }
    }


    private fun getCurrentSelectedFragment() =
        childFragmentManager.findFragmentByTag("f${binding.vpMediaPager.currentItem}")

    private fun setResult(position: Int) {
        setFragmentResult(POSITION, bundleOf(POSITION to position))
    }

    companion object {
        private const val ROOM_ID = "roomId"
        const val POSITION = "position"
        fun create(roomId: String, itemPosition: Int) = FullScreenPagerFragment().apply {
            arguments = bundleOf(ROOM_ID to roomId, POSITION to itemPosition)
        }
    }
}