package org.futo.circles.feature.share.circle

import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.room.select.RoomsPicker
import org.futo.circles.core.share.BaseShareActivity
import org.futo.circles.core.utils.getTimelineRoomFor
import org.futo.circles.feature.room.select.SelectRoomsFragment

@AndroidEntryPoint
class ShareWithCircleActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.share_with_circle

    override val roomsPicker: RoomsPicker =
        SelectRoomsFragment.create(CircleRoomTypeArg.Circle)

    override fun getShareRoomsIds(): List<String> =
        roomsPicker.getSelectedRooms().mapNotNull { getTimelineRoomFor(it.id)?.roomId }

}