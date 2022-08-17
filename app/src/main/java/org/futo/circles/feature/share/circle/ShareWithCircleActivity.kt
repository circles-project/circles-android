package org.futo.circles.feature.share.circle

import org.futo.circles.R
import org.futo.circles.extensions.getTimelineRoomFor
import org.futo.circles.feature.room.select.SelectRoomsFragment
import org.futo.circles.feature.share.BaseShareActivity

class ShareWithCircleActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.share_with_circle

    override val selectRoomsFragment: org.futo.circles.feature.photos.select.SelectRoomsFragment =
        SelectRoomsFragment()

    override fun getShareRoomsIds(): List<String> =
        selectRoomsFragment.getSelectedRooms().mapNotNull { getTimelineRoomFor(it.id)?.roomId }

}