package org.futo.circles.model


data class RoomEventGroupInfo(
    val roomId: String,
    val roomDisplayName: String = ""
) {
    var shouldBing: Boolean = false
    var customSound: String? = null
    var hasSmartReplyError: Boolean = false
    var isUpdated: Boolean = false
}
