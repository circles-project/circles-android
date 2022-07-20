package org.futo.circles.feature.photos.gallery

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
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.core.picker.MediaPickerHelper.Companion.IS_VIDEO_AVAILABLE
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.picker.PickGalleryMediaListener
import org.futo.circles.databinding.GalleryFragmentBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.photos.gallery.list.GalleryContentViewHolder
import org.futo.circles.feature.photos.gallery.list.GalleryItemsAdapter
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GalleryFragment : Fragment(R.layout.gallery_fragment) {

    private val args: GalleryFragmentArgs by navArgs()
    private val viewModel by viewModel<GalleryViewModel> {
        parametersOf(
            args.roomId, CircleRoomTypeArg.Photo,
            arguments?.getBoolean(IS_VIDEO_AVAILABLE, true) ?: true
        )
    }
    private val binding by viewBinding(GalleryFragmentBinding::bind)
    private val mediaPickerHelper = MediaPickerHelper(this, true)
    private val listAdapter by lazy {
        GalleryItemsAdapter(
            onGalleryItemClicked = { postId -> navigateToImagePreview(postId) },
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
            adapter = listAdapter
            addItemDecoration(BaseRvDecoration.OffsetDecoration<GalleryContentViewHolder>(2))
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
            success = { activity?.onBackPressed() }
        )
        viewModel.selectedImageUri.observeResponse(this,
            success = { pickMediaListener?.onMediaSelected(it, MediaType.Image) }
        )
    }

    private fun setupMenu() {
        activity?.addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menu.clear()
                (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
                menuInflater.inflate(R.menu.gallery_timeline_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                when (menuItem.itemId) {
                    R.id.configureGallery -> navigateToUpdateRoom()
                    R.id.deleteGallery -> showDeleteConfirmation()
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
            GalleryFragmentDirections.toUpdateRoomDialogFragment(args.roomId)
        )
    }

    private fun navigateToImagePreview(postId: String) {
        pickMediaListener?.let { viewModel.getImageUri(requireContext(), postId) } ?: kotlin.run {
            findNavController().navigate(
                GalleryFragmentDirections.toGalleryImageDialogFragment(args.roomId, postId)
            )
        }
    }

    private fun showDeleteConfirmation() {
        showDialog(
            titleResIdRes = R.string.delete_gallery,
            messageResId = R.string.delete_gallery_message,
            positiveButtonRes = R.string.delete,
            negativeButtonVisible = true,
            positiveAction = { viewModel.deleteGallery() }
        )
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
