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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.picker.MediaPickerHelper
import org.futo.circles.core.picker.PickGalleryImageListener
import org.futo.circles.core.list.BaseRvDecoration
import org.futo.circles.databinding.GalleryFragmentBinding
import org.futo.circles.extensions.*
import org.futo.circles.feature.photos.gallery.list.GalleryImageViewHolder
import org.futo.circles.feature.photos.gallery.list.GalleryImagesAdapter
import org.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class GalleryFragment : Fragment(R.layout.gallery_fragment) {

    private val args: GalleryFragmentArgs by navArgs()
    private val viewModel by viewModel<GalleryViewModel> {
        parametersOf(args.roomId, CircleRoomTypeArg.Photo)
    }
    private val binding by viewBinding(GalleryFragmentBinding::bind)
    private val mediaPickerHelper = MediaPickerHelper(this)
    private val listAdapter by lazy {
        GalleryImagesAdapter(
            onGalleryImageClicked = { postId -> navigateToImagePreview(postId) },
            onLoadMore = { viewModel.loadMore() })
    }

    private var pickImageListener: PickGalleryImageListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        pickImageListener = parentFragment as? PickGalleryImageListener
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.rvGallery.apply {
            adapter = listAdapter
            addItemDecoration(BaseRvDecoration.OffsetDecoration<GalleryImageViewHolder>(2))
            bindToFab(binding.fbUploadImage)
        }
        binding.fbUploadImage.setOnClickListener { showImagePicker() }
        binding.fbUploadImage.setIsVisible(pickImageListener == null)
    }

    private fun setupObservers() {
        viewModel.titleLiveData?.observeData(this) { title ->
            setToolbarTitle(title ?: "")
        }
        viewModel.galleryImagesLiveData.observeData(this) {
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
            success = { pickImageListener?.onImageSelected(it) }
        )
    }

    private fun showImagePicker() {
        mediaPickerHelper.showImagePickerDialog(onImageSelected = { _, uri ->
            viewModel.uploadImage(uri)
        })
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        (menu as? MenuBuilder)?.setOptionalIconsVisible(true)
        inflater.inflate(R.menu.gallery_timeline_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.configureGallery -> {
                navigateToUpdateRoom()
                return true
            }
            R.id.deleteGallery -> {
                showDeleteConfirmation()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun navigateToUpdateRoom() {
        findNavController().navigate(
            GalleryFragmentDirections.toUpdateRoomDialogFragment(args.roomId)
        )
    }

    private fun navigateToImagePreview(postId: String) {
        pickImageListener?.let { viewModel.getImageUri(requireContext(), postId) } ?: kotlin.run {
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
        fun create(roomId: String) = GalleryFragment().apply {
            arguments = bundleOf(ROOM_ID to roomId, TYPE to CircleRoomTypeArg.Photo)
        }
    }
}
