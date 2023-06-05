package org.futo.circles.core.share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import org.futo.circles.core.BaseActivity
import org.futo.circles.core.CirclesAppConfig
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ActivityBaseShareBinding
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.core.room.select.RoomsPicker
import org.futo.circles.core.room.select.SelectRoomsListener
import org.koin.androidx.viewmodel.ext.android.viewModel

abstract class BaseShareActivity : BaseActivity(R.layout.activity_base_share), SelectRoomsListener {

    private val viewModel by viewModel<BaseShareViewModel>()
    protected val binding by viewBinding(ActivityBaseShareBinding::bind, R.id.mainContainer)
    abstract val roomsPicker: RoomsPicker

    private var uriToShare: Uri? = null
    private var mediaType: MediaType = MediaType.Image

    abstract val titleResId: Int
    abstract fun getShareRoomsIds(): List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MatrixSessionProvider.currentSession?.let {
            addSelectRoomsFragment()
            handleIntent()
            setupViews()
            setupObservers()
        } ?: run {
            startActivity(packageManager.getLaunchIntentForPackage(CirclesAppConfig.appId))
            finish()
        }
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        binding.btnSave.isEnabled = rooms.isNotEmpty()
    }

    private fun addSelectRoomsFragment() {
        val fragment = (roomsPicker as? Fragment) ?: return
        supportFragmentManager.beginTransaction()
            .replace(R.id.lContainer, fragment)
            .commitAllowingStateLoss()
    }

    private fun setupViews() {
        binding.toolbar.title = getString(titleResId)
        binding.btnSave.setOnClickListener {
            uriToShare?.let {
                viewModel.uploadToRooms(it, getShareRoomsIds(), mediaType)
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