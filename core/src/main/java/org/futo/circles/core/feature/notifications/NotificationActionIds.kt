package org.futo.circles.core.feature.notifications

import org.futo.circles.core.base.CirclesAppConfig

object NotificationActionIds {

    val markRoomRead = "${CirclesAppConfig.appId}.NotificationActions.MARK_ROOM_READ_ACTION"
    val dismissRoom =
        "${CirclesAppConfig.appId}.NotificationActions.DISMISS_ROOM_NOTIF_ACTION"
    val diagnostic = "${CirclesAppConfig.appId}.NotificationActions.DIAGNOSTIC"
    val push = "${CirclesAppConfig.appId}.PUSH"

}