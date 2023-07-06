package org.futo.circles.gallery.feature.gallery.grid

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.setIsVisible
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.core.picker.DeviceMediaPickerHelper.Companion.IS_VIDEO_AVAILABLE
import org.futo.circles.core.picker.MediaType
import org.futo.circles.gallery.R
import org.futo.circles.gallery.databinding.FragmentGalleryBinding
import org.futo.circles.gallery.feature.gallery.GalleryMediaPreviewListener
import org.futo.circles.gallery.feature.gallery.grid.list.GalleryItemViewHolder
import org.futo.circles.gallery.feature.gallery.grid.list.GalleryItemsAdapter
import org.futo.circles.gallery.feature.pick.AllMediaPickerHelper
import org.futo.circles.gallery.feature.pick.PickGalleryMediaListener
import org.futo.circles.gallery.model.GalleryContentListItem

@AndroidEntryPoint
class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val viewModel by viewModels<GalleryViewModel>({ requireParentFragment() })
    private val binding by viewBinding(FragmentGalleryBinding::bind)
    private val mediaPickerHelper = AllMediaPickerHelper(this, true)
    private val listAdapter by lazy {
        GalleryItemsAdapter(
            onGalleryItemClicked = { item -> onMediaItemSelected(item) },
            onLoadMore = { viewModel.loadMore() })
    }

    private var pickMediaListener: PickGalleryMediaListener? = null
    private var previewMediaListener: GalleryMediaPreviewListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickMediaListener = parentFragment as? PickGalleryMediaListener
        previewMediaListener = parentFragment as? GalleryMediaPreviewListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvGallery.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                    gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
                }
            adapter = listAdapter
            addItemDecoration(BaseRvDecoration.OffsetDecoration<GalleryItemViewHolder>(2))
            bindToFab(binding.fbUploadImage)
        }
        binding.fbUploadImage.setOnClickListener { showImagePicker() }
        binding.fbUploadImage.setIsVisible(pickMediaListener == null)
    }

    private fun setupObservers() {
        viewModel.galleryItemsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }


    private fun showImagePicker() {
        mediaPickerHelper.showMediaPickerDialog(
            onImageSelected = { _, uri ->
                viewModel.uploadMedia(uri, MediaType.Image)
            },
            onVideoSelected = { uri ->
                viewModel.uploadMedia(uri, MediaType.Video)
            }
        )
    }

    private fun onMediaItemSelected(item: GalleryContentListItem) {
        pickMediaListener?.let {
            viewModel.selectMediaForPicker(requireContext(), item, it)
        } ?: previewMediaListener?.onPreviewMedia(item.id)
    }

    companion object {
        private const val ROOM_ID = "roomId"
        fun create(roomId: String, isVideoAvailable: Boolean) = GalleryFragment().apply {
            arguments = bundleOf(
                ROOM_ID to roomId,
                IS_VIDEO_AVAILABLE to isVideoAvailable
            )
        }
    }
}
