
package org.futo.circles.feature.notifications.model


data class RoomEventGroupInfo(
        val roomId: String,
        val roomDisplayName: String = "",
        val isDirect: Boolean = false
) {
    var hasNewEvent: Boolean = false
    var shouldBing: Boolean = false
    var customSound: String? = null
    var hasSmartReplyError: Boolean = false
    var isUpdated: Boolean = false
}
