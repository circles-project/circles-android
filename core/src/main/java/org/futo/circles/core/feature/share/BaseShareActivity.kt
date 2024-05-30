package org.futo.circles.core.feature.share

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import org.futo.circles.core.R
import org.futo.circles.core.base.BaseActivity
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.databinding.ActivityBaseShareBinding
import org.futo.circles.core.feature.room.select.RoomsPicker
import org.futo.circles.core.feature.room.select.SelectRoomsListener
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.model.SelectableRoomListItem
import org.futo.circles.core.provider.MatrixSessionProvider

abstract class BaseShareActivity : BaseActivity(), SelectRoomsListener {

    private lateinit var binding: ActivityBaseShareBinding

    private val viewModel by viewModels<BaseShareViewModel>()
    abstract val roomsPicker: RoomsPicker

    private var uriToShare: Uri? = null
    private var mediaType: MediaType = MediaType.Image

    abstract val titleResId: Int
    abstract fun getShareRoomsIds(): List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBaseShareBinding.inflate(layoutInflater)
        setContentView(binding.root)
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