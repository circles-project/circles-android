package org.futo.circles.feature.share.gallery

import org.futo.circles.R
import org.futo.circles.feature.photos.select.SelectGalleriesFragment
import org.futo.circles.feature.photos.select.SelectRoomsFragment
import org.futo.circles.feature.share.BaseShareActivity


class UploadToGalleryActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.upload_to_gallery
    override val selectRoomsFragment: SelectRoomsFragment = SelectGalleriesFragment()

    override fun getShareRoomsIds(): List<String> =
        selectRoomsFragment.getSelectedRooms().map { it.id }

}