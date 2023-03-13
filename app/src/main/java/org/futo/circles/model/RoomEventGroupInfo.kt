package org.futo.circles.model


data class RoomEventGroupInfo(
    val roomId: String,
    val roomDisplayName: String = ""
) {
    var isUpdated: Boolean = false
}
