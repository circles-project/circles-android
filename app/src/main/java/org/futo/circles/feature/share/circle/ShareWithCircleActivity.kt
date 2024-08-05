package org.futo.circles.feature.share.circle

import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.feature.room.select.SelectRoomsFragment
import org.futo.circles.core.feature.room.select.interfaces.RoomsPicker
import org.futo.circles.core.feature.share.BaseShareActivity
import org.futo.circles.core.model.SelectRoomTypeArg

@AndroidEntryPoint
class ShareWithCircleActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.share_with_circle

    override val roomsPicker: RoomsPicker =
        SelectRoomsFragment.create(SelectRoomTypeArg.MyCircles)

    override fun getShareRoomsIds(): List<String> = roomsPicker.getSelectedRooms().map { it.id }

}