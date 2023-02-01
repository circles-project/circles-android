package org.futo.circles.feature.notifications

import org.futo.circles.BuildConfig

object NotificationActionIds {

    private const val applicationId: String = BuildConfig.APPLICATION_ID

    const val markRoomRead = "$applicationId.NotificationActions.MARK_ROOM_READ_ACTION"
    const val dismissRoom = "$applicationId.NotificationActions.DISMISS_ROOM_NOTIF_ACTION"
    const val tapToView = "$applicationId.NotificationActions.TAP_TO_VIEW_ACTION"
    const val diagnostic = "$applicationId.NotificationActions.DIAGNOSTIC"
    const val push = "$applicationId.PUSH"

}