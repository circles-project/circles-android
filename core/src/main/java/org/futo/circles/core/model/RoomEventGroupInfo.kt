package org.futo.circles.core.model


data class RoomEventGroupInfo(
    val roomId: String,
    val roomDisplayName: String = ""
) {
    var isUpdated: Boolean = false
}
