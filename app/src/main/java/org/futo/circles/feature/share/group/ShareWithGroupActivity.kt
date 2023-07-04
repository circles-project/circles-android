package org.futo.circles.feature.share.group

import dagger.hilt.android.AndroidEntryPoint
import org.futo.circles.R
import org.futo.circles.core.model.CircleRoomTypeArg
import org.futo.circles.core.room.select.RoomsPicker
import org.futo.circles.core.share.BaseShareActivity
import org.futo.circles.feature.room.select.SelectRoomsFragment

@AndroidEntryPoint
class ShareWithGroupActivity : BaseShareActivity() {

    override val titleResId: Int = R.string.share_with_group

    override val roomsPicker: RoomsPicker = SelectRoomsFragment.create(CircleRoomTypeArg.Group)

    override fun getShareRoomsIds(): List<String> = roomsPicker.getSelectedRooms().map { it.id }

}