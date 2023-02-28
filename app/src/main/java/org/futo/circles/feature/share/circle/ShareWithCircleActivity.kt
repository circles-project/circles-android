package org.futo.circles.feature.share.circle

import org.futo.circles.R
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.feature.room.select.SelectRoomsFragment
import org.futo.circles.feature.share.BaseShareActivity
import org.futo.circles.model.CircleRoomTypeArg

class ShareWithCircleActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.share_with_circle

    override val roomsPicker: org.futo.circles.feature.photos.select.RoomsPicker =
        SelectRoomsFragment.create(CircleRoomTypeArg.Circle)

    override fun getShareRoomsIds(): List<String> =
        roomsPicker.getSelectedRooms().mapNotNull { getTimelineRoomFor(it.id)?.roomId }

}