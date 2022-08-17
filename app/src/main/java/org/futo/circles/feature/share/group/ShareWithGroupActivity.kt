package org.futo.circles.feature.share.group

import org.futo.circles.R
import org.futo.circles.feature.photos.select.RoomsPicker
import org.futo.circles.feature.room.select.SelectRoomsFragment
import org.futo.circles.feature.share.BaseShareActivity
import org.futo.circles.model.CircleRoomTypeArg

class ShareWithGroupActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.share_with_group

    override val roomsPicker: RoomsPicker = SelectRoomsFragment.create(CircleRoomTypeArg.Group)

    override fun getShareRoomsIds(): List<String> = roomsPicker.getSelectedRooms().map { it.id }

}