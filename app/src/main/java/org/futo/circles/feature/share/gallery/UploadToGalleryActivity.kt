package org.futo.circles.feature.share.gallery

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.picker.MediaType
import org.futo.circles.databinding.ActivityUploadToGalleryBinding
import org.futo.circles.feature.photos.select.SelectGalleriesFragment
import org.futo.circles.feature.photos.select.SelectGalleriesListener
import org.futo.circles.model.SelectableRoomListItem
import org.koin.androidx.viewmodel.ext.android.viewModel


class UploadToGalleryActivity : AppCompatActivity(R.layout.activity_upload_to_gallery),
    SelectGalleriesListener {

    private val binding by viewBinding(ActivityUploadToGalleryBinding::bind, R.id.mainContainer)

    private val selectedGalleriesFragment by lazy { SelectGalleriesFragment() }

    private val viewModel by viewModel<UploadToGalleryViewModel>()

    private var uriToShare: Uri? = null
    private var mediaType: MediaType = MediaType.Image


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSelectGalleriesFragment()
        setupViews()
        setupObservers()
        handleIntent()
    }

    private fun addSelectGalleriesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectedGalleriesFragment)
            .commitAllowingStateLoss()
    }

    private fun setupViews() {
        binding.btnSave.setOnClickListener {
            uriToShare?.let {
                viewModel.uploadToGalleries(
                    it, selectedGalleriesFragment.getSelectedGalleries(), mediaType
                )
            }
        }
    }

    private fun setupObservers() {
        viewModel.saveResultLiveData.observe(this) { finish() }
    }

    override fun onGallerySelected(galleries: List<SelectableRoomListItem>) {
        binding.btnSave.isEnabled = galleries.isNotEmpty()
    }

    private fun handleIntent() {
        uriToShare = intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        mediaType =
            if (intent?.type?.startsWith("image/") == true) MediaType.Image else MediaType.Video

    }
    
}