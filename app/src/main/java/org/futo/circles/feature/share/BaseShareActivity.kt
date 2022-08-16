package org.futo.circles.feature.share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.R
import org.futo.circles.core.BaseActivity
import org.futo.circles.core.picker.MediaType
import org.futo.circles.databinding.ActivityBaseShareBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseShareActivity : BaseActivity(R.layout.activity_base_share) {

    private val viewModel by viewModel<BaseShareViewModel>()
    protected val binding by viewBinding(ActivityBaseShareBinding::bind, R.id.mainContainer)

    private var uriToShare: Uri? = null
    private var mediaType: MediaType = MediaType.Image

    abstract val titleResId: Int
    abstract fun getSelectedRoomsIds(): List<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIntent()
        setupViews()
        setupObservers()
    }
    
    private fun setupViews() {
        binding.toolbar.title = getString(titleResId)
        binding.btnSave.setOnClickListener {
            uriToShare?.let {
                viewModel.uploadToRooms(it, getSelectedRoomsIds(), mediaType)
            }
        }
    }

    private fun setupObservers() {
        viewModel.saveResultLiveData.observe(this) { finish() }
    }

    @Suppress("DEPRECATION")
    private fun handleIntent() {
        uriToShare = intent?.getParcelableExtra(Intent.EXTRA_STREAM)
        mediaType =
            if (intent?.type?.startsWith("image/") == true) MediaType.Image else MediaType.Video

    }
}