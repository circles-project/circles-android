package org.futo.circles.feature.photos.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.view.menu.MenuBuilder
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.PickGalleryMediaListener
import org.futo.circles.core.extensions.observeData
import org.futo.circles.core.extensions.observeResponse
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.core.picker.MediaPickerHelper.Companion.IS_VIDEO_AVAILABLE
import org.futo.circles.core.picker.MediaType
import org.futo.circles.databinding.FragmentGalleryBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.photos.gallery.list.GalleryItemViewHolder
import org.futo.circles.feature.photos.gallery.list.GalleryItemsAdapter
import org.futo.circles.model.CircleRoomTypeArg
import org.futo.circles.model.ConfirmationType
import org.futo.circles.model.GalleryContentListItem
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val args: GalleryFragmentArgs by navArgs()
    private val viewModel by viewModel<GalleryViewModel> {
        parametersOf(
            args.roomId, CircleRoomTypeArg.Photo,
            arguments?.getBoolean(IS_VIDEO_AVAILABLE, true) ?: true
        )
    }
    private val binding by viewBinding(FragmentGalleryBinding::bind)
    private val mediaPickerHelper = MediaPickerHelper(this, true)
    private val listAdapter by lazy {
        GalleryItemsAdapter(
            onGalleryItemClicked = { item -> onMediaItemSelected(item) },
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
        setupMenu()
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
        viewModel.titleLiveData?.observeData(this) { title ->
            setToolbarTitle(title ?: "")
        }
        viewModel.galleryItemsLiveData.observeData(this) {
            listAdapter.submitList(it)
        }
        viewModel.scrollToTopLiveData.observeData(this) {
            binding.rvGallery.postDelayed(
                { binding.rvGallery.scrollToPosition(0) }, 500
            )
        }
        viewModel.deleteGalleryLiveData.observeResponse(this,
            success = { onBackPressed() }
        )
    }

    private fun setupMenu() {
        activity?.addMenuProvider(object : MenuProvider {
            @SuppressLint("RestrictedApi")
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
                menuInflater.inflate(R.menu.gallery_timeline_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.configureGallery -> navigateToUpdateRoom()
                    R.id.deleteGallery -> withConfirmation(ConfirmationType.DELETE_GALLERY) { viewModel.deleteGallery() }
                }
                return true
            }
        }, viewLifecycleOwner)
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

    private fun navigateToUpdateRoom() {
        findNavController().navigate(
            GalleryFragmentDirections.toUpdateGalleryDialogFragment(args.roomId)
        )
    }

    private fun onMediaItemSelected(item: GalleryContentListItem) {
        pickMediaListener?.let {
            viewModel.selectMediaForPicker(requireContext(), item, it)
        } ?: kotlin.run {
            findNavController().navigate(
                GalleryFragmentDirections.toGalleryImageDialogFragment(args.roomId, item.id)
            )
        }
    }

    companion object {
        private const val ROOM_ID = "roomId"
        private const val TYPE = "type"
        fun create(roomId: String, isVideoAvailable: Boolean) = GalleryFragment().apply {
            arguments = bundleOf(
                ROOM_ID to roomId,
                TYPE to CircleRoomTypeArg.Photo,
                IS_VIDEO_AVAILABLE to isVideoAvailable
            )
        }
    }
}
