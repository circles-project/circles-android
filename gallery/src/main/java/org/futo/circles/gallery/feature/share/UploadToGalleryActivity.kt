package org.futo.circles.gallery.feature.share

import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.core.feature.room.select.RoomsPicker
import org.futo.circles.core.feature.share.BaseShareActivity
import org.futo.circles.gallery.R
import org.futo.circles.gallery.feature.select.SelectGalleriesFragment

@AndroidEntryPoint
class UploadToGalleryActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.upload_to_gallery
    override val roomsPicker: RoomsPicker = SelectGalleriesFragment()

    override fun getShareRoomsIds(): List<String> = roomsPicker.getSelectedRooms().map { it.id }

}