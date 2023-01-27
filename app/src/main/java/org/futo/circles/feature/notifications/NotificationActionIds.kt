package org.futo.circles.feature.notifications

import org.futo.circles.BuildConfig

object NotificationActionIds {

    private const val applicationId: String = BuildConfig.APPLICATION_ID

    val join = "$applicationId.NotificationActions.JOIN_ACTION"
    val reject = "$applicationId.NotificationActions.REJECT_ACTION"
    val quickLaunch = "$applicationId.NotificationActions.QUICK_LAUNCH_ACTION"
    val markRoomRead = "$applicationId.NotificationActions.MARK_ROOM_READ_ACTION"
    val smartReply = "$applicationId.NotificationActions.SMART_REPLY_ACTION"
    val dismissSummary = "$applicationId.NotificationActions.DISMISS_SUMMARY_ACTION"
    val dismissRoom = "$applicationId.NotificationActions.DISMISS_ROOM_NOTIF_ACTION"
    val tapToView = "$applicationId.NotificationActions.TAP_TO_VIEW_ACTION"
    val diagnostic = "$applicationId.NotificationActions.DIAGNOSTIC"
    val push = "$applicationId.PUSH"

}