package org.futo.circles.core.picker.gallery.media

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.R
import org.futo.circles.core.databinding.FragmentPickGalleryBinding
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.core.model.GalleryContentListItem
import org.futo.circles.core.picker.gallery.PickGalleryMediaListener
import org.futo.circles.core.picker.gallery.media.list.GalleryMediaGridAdapter
import org.futo.circles.core.picker.gallery.media.list.GalleryMediaItemViewHolder
import org.futo.circles.core.picker.helper.MediaPickerHelper.Companion.IS_VIDEO_AVAILABLE


@AndroidEntryPoint
class PickMediaItemFragment : Fragment(R.layout.fragment_pick_gallery) {

    private val viewModel by viewModels<PickMediaItemViewModel>()
    private val binding by viewBinding(FragmentPickGalleryBinding::bind)
    private val listAdapter by lazy {
        GalleryMediaGridAdapter(
            onMediaItemClicked = { item -> onMediaItemSelected(item) },
            onLoadMore = { viewModel.loadMore() })
    }

    private var pickMediaListener: PickGalleryMediaListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickMediaListener = parentFragment as? PickGalleryMediaListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
    }


    private fun setupViews() {
        binding.rvRooms.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = listAdapter
            addItemDecoration(BaseRvDecoration.OffsetDecoration<GalleryMediaItemViewHolder>(2))
        }
    }

    private fun setupObservers() {
        viewModel.galleryItemsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
    }


    private fun onMediaItemSelected(item: GalleryContentListItem) {
        pickMediaListener?.let { viewModel.selectMediaForPicker(requireContext(), item, it) }
    }

    companion object {
        private const val ROOM_ID = "roomId"
        fun create(roomId: String, isVideoAvailable: Boolean) = PickMediaItemFragment().apply {
            arguments = bundleOf(
                ROOM_ID to roomId,
                IS_VIDEO_AVAILABLE to isVideoAvailable
            )
        }
    }
}
