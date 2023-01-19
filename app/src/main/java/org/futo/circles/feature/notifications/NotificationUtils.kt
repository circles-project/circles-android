@file:Suppress("UNUSED_PARAMETER")

package org.futo.circles.feature.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.RemoteInput
import androidx.core.app.TaskStackBuilder
import androidx.core.content.ContextCompat
import org.futo.circles.MainActivity
import org.futo.circles.R
import org.futo.circles.feature.notifications.model.InviteNotifiableEvent
import org.futo.circles.feature.notifications.model.RoomEventGroupInfo
import org.futo.circles.feature.notifications.model.SimpleNotifiableEvent
import kotlin.random.Random

//TODO singleton
class NotificationUtils(
    private val context: Context,
    private val actionIds: NotificationActionIds
) {

    companion object {
        const val NOTIFICATION_ID_FOREGROUND_SERVICE = 61
        private const val LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID =
            "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID"
        private const val NOISY_NOTIFICATION_CHANNEL_ID = "DEFAULT_NOISY_NOTIFICATION_CHANNEL_ID"
        const val SILENT_NOTIFICATION_CHANNEL_ID = "DEFAULT_SILENT_NOTIFICATION_CHANNEL_ID_V2"

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
        fun supportNotificationChannels() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    }

    private val notificationManager = NotificationManagerCompat.from(context)


    fun createNotificationChannels() {
        if (!supportNotificationChannels()) {
            return
        }

        val accentColor = ContextCompat.getColor(context, R.color.blue)

        notificationManager.createNotificationChannel(NotificationChannel(
            NOISY_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_noisy_notifications)
                .ifEmpty { "Noisy notifications" },
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .apply {
                description = context.getString(R.string.notification_noisy_notifications)
                enableVibration(true)
                enableLights(true)
                lightColor = accentColor
            })

        /**
         * Low notification importance: shows everywhere, but is not intrusive.
         */
        notificationManager.createNotificationChannel(NotificationChannel(
            SILENT_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_silent_notifications)
                .ifEmpty { "Silent notifications" },
            NotificationManager.IMPORTANCE_LOW
        )
            .apply {
                description = context.getString(R.string.notification_silent_notifications)
                setSound(null, null)
                enableLights(true)
                lightColor = accentColor
            })

        notificationManager.createNotificationChannel(NotificationChannel(
            LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_listening_for_events)
                .ifEmpty { "Listening for events" },
            NotificationManager.IMPORTANCE_MIN
        )
            .apply {
                description = context.getString(R.string.notification_listening_for_events)
                setSound(null, null)
                setShowBadge(false)
            })
    }

    fun getChannel(channelId: String): NotificationChannel? {
        return notificationManager.getNotificationChannel(channelId)
    }

    fun buildForegroundServiceNotification(
        @StringRes subTitleResId: Int,
        withProgress: Boolean = true
    ): Notification {
        // build the pending intent go to the home screen if this is clicked.
        val i = getMainIntent(context)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val mainIntent = getMainIntent(context)
        val pi =
            PendingIntent.getActivity(context, 0, mainIntent, PendingIntentCompat.FLAG_IMMUTABLE)

        val accentColor = ContextCompat.getColor(context, R.color.blue)

        val builder =
            NotificationCompat.Builder(context, LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(subTitleResId))
                .setSmallIcon(R.drawable.ic_check)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setColor(accentColor)
                .setContentIntent(pi)
                .apply {
                    if (withProgress) {
                        setProgress(0, 0, true)
                    }
                }

        // PRIORITY_MIN should not be used with Service#startForeground(int, Notification)
        builder.priority = NotificationCompat.PRIORITY_LOW
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//            builder.priority = NotificationCompat.PRIORITY_MIN
//        }

        val notification = builder.build()

        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR

        return notification
    }

    /**
     * Creates a notification that indicates the application is initializing.
     */
    fun buildStartAppNotification(): Notification {
        return NotificationCompat.Builder(context, LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID)
            .setContentTitle(context.getString(R.string.updating_your_data))
            .setSmallIcon(R.drawable.ic_check)
            .setColor(ContextCompat.getColor(context, R.color.blue))
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    /**
     * Build a notification for a Room.
     */
    fun buildMessagesListNotification(
        messageStyle: NotificationCompat.MessagingStyle,
        roomInfo: RoomEventGroupInfo,
        threadId: String?,
        largeIcon: Bitmap?,
        lastMessageTimestamp: Long,
        senderDisplayNameForReplyCompat: String?,
        tickerText: String
    ): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.blue)
        // Build the pending intent for when the notification is clicked
        val openIntent = buildOpenRoomIntent(roomInfo.roomId)

        val smallIcon = R.drawable.ic_check

        val channelID =
            if (roomInfo.shouldBing) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID
        return NotificationCompat.Builder(context, channelID)
            .setOnlyAlertOnce(roomInfo.isUpdated)
            .setWhen(lastMessageTimestamp)
            .setStyle(messageStyle)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setShortcutId(roomInfo.roomId)
            .setContentTitle(roomInfo.roomDisplayName)
            .setContentText(context.getString(R.string.notification_new_messages))
            .setSubText(
                context.resources.getQuantityString(
                    R.plurals.room_new_messages_notification,
                    messageStyle.messages.size,
                    messageStyle.messages.size
                )
            )
            .setGroup(context.getString(R.string.app_name))
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            .setSmallIcon(smallIcon)
            .setColor(accentColor)
            .apply {
                if (roomInfo.shouldBing) {
                    // Compat
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    setLights(accentColor, 500, 500)
                } else {
                    priority = NotificationCompat.PRIORITY_LOW
                }

                // Add actions and notification intents
                // Mark room as read
                val markRoomReadIntent = Intent(context, NotificationBroadcastReceiver::class.java)
                markRoomReadIntent.action = actionIds.markRoomRead
                markRoomReadIntent.data = createIgnoredUri(roomInfo.roomId)
                markRoomReadIntent.putExtra(
                    NotificationBroadcastReceiver.KEY_ROOM_ID,
                    roomInfo.roomId
                )
                val markRoomReadPendingIntent = PendingIntent.getBroadcast(
                    context,
                    System.currentTimeMillis().toInt(),
                    markRoomReadIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )

                NotificationCompat.Action.Builder(
                    R.drawable.ic_check,
                    context.getString(R.string.action_mark_room_read),
                    markRoomReadPendingIntent
                )
                    .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_MARK_AS_READ)
                    .setShowsUserInterface(false)
                    .build()
                    .let { addAction(it) }

                // Quick reply
                if (!roomInfo.hasSmartReplyError) {
                    buildQuickReplyIntent(
                        roomInfo.roomId,
                        threadId,
                        senderDisplayNameForReplyCompat
                    )?.let { replyPendingIntent ->
                        val remoteInput =
                            RemoteInput.Builder(NotificationBroadcastReceiver.KEY_TEXT_REPLY)
                                .setLabel(context.getString(R.string.action_quick_reply))
                                .build()
                        NotificationCompat.Action.Builder(
                            R.drawable.ic_check,
                            context.getString(R.string.action_quick_reply),
                            replyPendingIntent
                        )
                            .addRemoteInput(remoteInput)
                            .setSemanticAction(NotificationCompat.Action.SEMANTIC_ACTION_REPLY)
                            .setShowsUserInterface(false)
                            .build()
                            .let { addAction(it) }
                    }
                }

                if (openIntent != null) {
                    setContentIntent(openIntent)
                }

                if (largeIcon != null) {
                    setLargeIcon(largeIcon)
                }

                val intent = Intent(context, NotificationBroadcastReceiver::class.java)
                intent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomInfo.roomId)
                intent.action = actionIds.dismissRoom
                val pendingIntent = PendingIntent.getBroadcast(
                    context.applicationContext,
                    System.currentTimeMillis().toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )
                setDeleteIntent(pendingIntent)
            }
            .setTicker(tickerText)
            .build()
    }

    fun buildRoomInvitationNotification(
        inviteNotifiableEvent: InviteNotifiableEvent,
        matrixId: String
    ): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.blue)
        // Build the pending intent for when the notification is clicked
        val smallIcon = R.drawable.ic_check

        val channelID =
            if (inviteNotifiableEvent.noisy) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID

        return NotificationCompat.Builder(context, channelID)
            .setOnlyAlertOnce(true)
            .setContentTitle(
                inviteNotifiableEvent.roomName ?: context.getString(R.string.app_name)
            )
            .setContentText(inviteNotifiableEvent.description)
            .setGroup(context.getString(R.string.app_name))
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            .setSmallIcon(smallIcon)
            .setColor(accentColor)
            .apply {
                val roomId = inviteNotifiableEvent.roomId
                // offer to type a quick reject button
                val rejectIntent = Intent(context, NotificationBroadcastReceiver::class.java)
                rejectIntent.action = actionIds.reject
                rejectIntent.data = createIgnoredUri("$roomId&$matrixId")
                rejectIntent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
                val rejectIntentPendingIntent = PendingIntent.getBroadcast(
                    context,
                    System.currentTimeMillis().toInt(),
                    rejectIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )

                addAction(
                    R.drawable.ic_check,
                    context.getString(R.string.action_reject),
                    rejectIntentPendingIntent
                )

                // offer to type a quick accept button
                val joinIntent = Intent(context, NotificationBroadcastReceiver::class.java)
                joinIntent.action = actionIds.join
                joinIntent.data = createIgnoredUri("$roomId&$matrixId")
                joinIntent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
                val joinIntentPendingIntent = PendingIntent.getBroadcast(
                    context,
                    System.currentTimeMillis().toInt(),
                    joinIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
                )
                addAction(
                    R.drawable.ic_check,
                    context.getString(R.string.action_join),
                    joinIntentPendingIntent
                )

                val contentIntent = getMainIntent(context)
                contentIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                // pending intent get reused by system, this will mess up the extra params, so put unique info to avoid that
                contentIntent.data = createIgnoredUri(inviteNotifiableEvent.eventId)
                setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        contentIntent,
                        PendingIntentCompat.FLAG_IMMUTABLE
                    )
                )

                if (inviteNotifiableEvent.noisy) {
                    // Compat
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    setLights(accentColor, 500, 500)
                } else {
                    priority = NotificationCompat.PRIORITY_LOW
                }
                setAutoCancel(true)
            }
            .build()
    }

    fun buildSimpleEventNotification(
        simpleNotifiableEvent: SimpleNotifiableEvent,
        matrixId: String
    ): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.blue)
        // Build the pending intent for when the notification is clicked
        val smallIcon = R.drawable.ic_check

        val channelID =
            if (simpleNotifiableEvent.noisy) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID

        return NotificationCompat.Builder(context, channelID)
            .setOnlyAlertOnce(true)
            .setContentTitle(context.getString(R.string.app_name))
            .setContentText(simpleNotifiableEvent.description)
            .setGroup(context.getString(R.string.app_name))
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            .setSmallIcon(smallIcon)
            .setColor(accentColor)
            .setAutoCancel(true)
            .apply {
                val contentIntent = getMainIntent(context)
                contentIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                // pending intent get reused by system, this will mess up the extra params, so put unique info to avoid that
                contentIntent.data = createIgnoredUri(simpleNotifiableEvent.eventId)
                setContentIntent(
                    PendingIntent.getActivity(
                        context,
                        0,
                        contentIntent,
                        PendingIntentCompat.FLAG_IMMUTABLE
                    )
                )

                if (simpleNotifiableEvent.noisy) {
                    // Compat
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    setLights(accentColor, 500, 500)
                } else {
                    priority = NotificationCompat.PRIORITY_LOW
                }
                setAutoCancel(true)
            }
            .build()
    }

    private fun buildOpenRoomIntent(roomId: String): PendingIntent? {
        val roomIntentTap = getMainIntent(context)
        roomIntentTap.action = actionIds.tapToView
        // pending intent get reused by system, this will mess up the extra params, so put unique info to avoid that
        roomIntentTap.data = createIgnoredUri("openRoom?$roomId")

        // Recreate the back stack
        return TaskStackBuilder.create(context)
            .addNextIntentWithParentStack(getMainIntent(context))
            .addNextIntent(roomIntentTap)
            .getPendingIntent(
                System.currentTimeMillis().toInt(),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
            )
    }

    private fun buildOpenHomePendingIntentForSummary(): PendingIntent {
        val intent = getMainIntent(context)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.data = createIgnoredUri("tapSummary")
        val mainIntent = getMainIntent(context)
        return PendingIntent.getActivity(
            context,
            Random.nextInt(1000),
            mainIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )
    }

    /*
        Direct reply is new in Android N, and Android already handles the UI, so the right pending intent
        here will ideally be a Service/IntentService (for a long running background task) or a BroadcastReceiver,
         which runs on the UI thread. It also works without unlocking, making the process really fluid for the user.
        However, for Android devices running Marshmallow and below (API level 23 and below),
        it will be more appropriate to use an activity. Since you have to provide your own UI.
     */
    private fun buildQuickReplyIntent(
        roomId: String,
        threadId: String?,
        senderName: String?
    ): PendingIntent? {
        val intent: Intent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent = Intent(context, NotificationBroadcastReceiver::class.java)
            intent.action = actionIds.smartReply
            intent.data = createIgnoredUri(roomId)
            intent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomId)
            threadId?.let {
                intent.putExtra(NotificationBroadcastReceiver.KEY_THREAD_ID, it)
            }

            return PendingIntent.getBroadcast(
                context,
                System.currentTimeMillis().toInt(),
                intent,
                // PendingIntents attached to actions with remote inputs must be mutable
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_MUTABLE
            )
        } else {
            /*
            TODO
            if (!LockScreenActivity.isDisplayingALockScreenActivity()) {
                // start your activity for Android M and below
                val quickReplyIntent = Intent(context, LockScreenActivity::class.java)
                quickReplyIntent.putExtra(LockScreenActivity.EXTRA_ROOM_ID, roomId)
                quickReplyIntent.putExtra(LockScreenActivity.EXTRA_SENDER_NAME, senderName ?: "")

                // the action must be unique else the parameters are ignored
                quickReplyIntent.action = QUICK_LAUNCH_ACTION
                quickReplyIntent.data = createIgnoredUri($roomId")
                return PendingIntent.getActivity(context, 0, quickReplyIntent, PendingIntentCompat.FLAG_IMMUTABLE)
            }
             */
        }
        return null
    }

    // // Number of new notifications for API <24 (M and below) devices.
    /**
     * Build the summary notification.
     */
    fun buildSummaryListNotification(
        style: NotificationCompat.InboxStyle?,
        compatSummary: String,
        noisy: Boolean,
        lastMessageTimestamp: Long
    ): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.blue)
        val smallIcon = R.drawable.ic_check

        return NotificationCompat.Builder(
            context,
            if (noisy) NOISY_NOTIFICATION_CHANNEL_ID else SILENT_NOTIFICATION_CHANNEL_ID
        )
            .setOnlyAlertOnce(true)
            // used in compat < N, after summary is built based on child notifications
            .setWhen(lastMessageTimestamp)
            .setStyle(style)
            .setContentTitle(context.getString(R.string.app_name))
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setSmallIcon(smallIcon)
            // set content text to support devices running API level < 24
            .setContentText(compatSummary)
            .setGroup(context.getString(R.string.app_name))
            // set this notification as the summary for the group
            .setGroupSummary(true)
            .setColor(accentColor)
            .apply {
                if (noisy) {
                    // Compat
                    priority = NotificationCompat.PRIORITY_DEFAULT
                    setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                    setLights(accentColor, 500, 500)
                } else {
                    // compat
                    priority = NotificationCompat.PRIORITY_LOW
                }
            }
            .setContentIntent(buildOpenHomePendingIntentForSummary())
            .setDeleteIntent(getDismissSummaryPendingIntent())
            .build()
    }

    private fun getDismissSummaryPendingIntent(): PendingIntent {
        val intent = Intent(context, NotificationBroadcastReceiver::class.java)
        intent.action = actionIds.dismissSummary
        intent.data = createIgnoredUri("deleteSummary")
        return PendingIntent.getBroadcast(
            context.applicationContext,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntentCompat.FLAG_IMMUTABLE
        )
    }
}

fun getMainIntent(context: Context): Intent {
    return Intent(context, MainActivity::class.java)
}

const val IGNORED_SCHEMA = "ignored"

fun createIgnoredUri(path: String): Uri = Uri.parse("$IGNORED_SCHEMA://$path")
