package org.futo.circles.feature.share.gallery

import android.os.Bundle
import org.futo.circles.R
import org.futo.circles.core.SelectRoomsListener
import org.futo.circles.feature.photos.select.SelectGalleriesFragment
import org.futo.circles.feature.share.BaseShareActivity
import org.futo.circles.model.SelectableRoomListItem


class UploadToGalleryActivity : BaseShareActivity(), SelectRoomsListener {

    private val selectedGalleriesFragment by lazy { SelectGalleriesFragment() }
    override val titleResId: Int = R.string.upload_to_gallery

    override fun getSelectedRoomsIds(): List<String> =
        selectedGalleriesFragment.getSelectedGalleries().map { it.id }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addSelectGalleriesFragment()
    }

    private fun addSelectGalleriesFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.lContainer, selectedGalleriesFragment)
            .commitAllowingStateLoss()
    }

    override fun onRoomsSelected(rooms: List<SelectableRoomListItem>) {
        binding.btnSave.isEnabled = rooms.isNotEmpty()
    }
}