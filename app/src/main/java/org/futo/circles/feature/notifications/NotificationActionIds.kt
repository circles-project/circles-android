package org.futo.circles.feature.notifications

import org.futo.circles.BuildConfig

object NotificationActionIds {

    private const val applicationId: String = BuildConfig.APPLICATION_ID

    val markRoomRead = "$applicationId.NotificationActions.MARK_ROOM_READ_ACTION"
    val dismissRoom = "$applicationId.NotificationActions.DISMISS_ROOM_NOTIF_ACTION"
    val tapToView = "$applicationId.NotificationActions.TAP_TO_VIEW_ACTION"
    val diagnostic = "$applicationId.NotificationActions.DIAGNOSTIC"
    val push = "$applicationId.PUSH"

}