package org.futo.circles.core.feature.notifications

import android.annotation.SuppressLint
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
import androidx.core.content.ContextCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.core.R
import org.futo.circles.core.base.CirclesAppConfig
import org.futo.circles.core.extensions.getBitmap
import org.futo.circles.core.feature.notifications.test.task.TestNotificationReceiver
import org.futo.circles.core.model.InviteNotifiableEvent
import org.futo.circles.core.model.RoomEventGroupInfo
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationUtils @Inject constructor(
    @ApplicationContext private val context: Context
) {

    companion object {
        const val NOTIFICATION_ID_FOREGROUND_SERVICE = 61
        private const val LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID =
            "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID"
        private const val ROOM_NOTIFICATION_CHANNEL_ID = "DEFAULT_ROOM_NOTIFICATION_CHANNEL_ID"
        private const val INVITE_NOTIFICATION_CHANNEL_ID = "DEFAULT_INVITE_NOTIFICATION_CHANNEL_ID"

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
        fun supportNotificationChannels() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    }

    private val notificationManager = NotificationManagerCompat.from(context)


    fun createNotificationChannels() {
        if (!supportNotificationChannels()) return

        val accentColor = ContextCompat.getColor(context, R.color.blue)

        notificationManager.createNotificationChannel(NotificationChannel(
            ROOM_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_room_notifications),
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .apply {
                description = context.getString(R.string.notification_room_notifications)
                enableVibration(true)
                enableLights(true)
                lightColor = accentColor
            })

        notificationManager.createNotificationChannel(NotificationChannel(
            INVITE_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_invitations),
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .apply {
                description = context.getString(R.string.notification_invitations)
                enableVibration(true)
                enableLights(true)
                lightColor = accentColor
            })

        notificationManager.createNotificationChannel(NotificationChannel(
            LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_listening_for_events),
            NotificationManager.IMPORTANCE_MIN
        )
            .apply {
                description = context.getString(R.string.notification_listening_for_events)
                setSound(null, null)
                setShowBadge(false)
            })
    }

    fun buildForegroundServiceNotification(
        @StringRes subTitleResId: Int,
        withProgress: Boolean = true
    ): Notification {
        val i = getMainIntent(context)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val mainIntent = getMainIntent(context)
        val pi = PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)
        val accentColor = ContextCompat.getColor(context, R.color.blue)
        val builder =
            NotificationCompat.Builder(context, LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(subTitleResId))
                .setSmallIcon(R.drawable.ic_push_notification)
                .setCategory(NotificationCompat.CATEGORY_SERVICE)
                .setColor(accentColor)
                .setContentIntent(pi)
                .apply {
                    if (withProgress) {
                        setProgress(0, 0, true)
                    }
                }
        builder.priority = NotificationCompat.PRIORITY_LOW
        val notification = builder.build()

        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR

        return notification
    }


    fun buildMessagesListNotification(
        messageStyle: NotificationCompat.MessagingStyle,
        roomInfo: RoomEventGroupInfo,
        largeIcon: Bitmap?,
        lastMessageTimestamp: Long,
        tickerText: String
    ): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.blue)
        return NotificationCompat.Builder(context, ROOM_NOTIFICATION_CHANNEL_ID)
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
            .setGroup(CirclesAppConfig.appName)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            .setSmallIcon(R.drawable.ic_push_notification)
            .setColor(accentColor)
            .setAutoCancel(true)
            .apply {
                priority = NotificationCompat.PRIORITY_DEFAULT
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                setLights(accentColor, 500, 500)


                val markRoomReadIntent = Intent(context, NotificationBroadcastReceiver::class.java)
                markRoomReadIntent.action = NotificationActionIds.markRoomRead
                markRoomReadIntent.data = createIgnoredUri(roomInfo.roomId)
                markRoomReadIntent.putExtra(
                    NotificationBroadcastReceiver.KEY_ROOM_ID,
                    roomInfo.roomId
                )
                val markRoomReadPendingIntent = PendingIntent.getBroadcast(
                    context,
                    System.currentTimeMillis().toInt(),
                    markRoomReadIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
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

                setContentIntent(buildNotificationClickIntent(roomInfo.roomId))
                if (largeIcon != null) setLargeIcon(largeIcon)

                val intent = Intent(context, NotificationBroadcastReceiver::class.java)
                intent.putExtra(NotificationBroadcastReceiver.KEY_ROOM_ID, roomInfo.roomId)
                intent.action = NotificationActionIds.dismissRoom
                val pendingIntent = PendingIntent.getBroadcast(
                    context.applicationContext,
                    System.currentTimeMillis().toInt(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                setDeleteIntent(pendingIntent)
            }
            .setTicker(tickerText)
            .build()
    }

    fun buildRoomInvitationNotification(
        inviteNotifiableEvent: InviteNotifiableEvent
    ): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.blue)
        return NotificationCompat.Builder(context, INVITE_NOTIFICATION_CHANNEL_ID)
            .setOnlyAlertOnce(true)
            .setContentTitle(inviteNotifiableEvent.roomName ?: CirclesAppConfig.appName)
            .setContentText(inviteNotifiableEvent.description)
            .setGroup(CirclesAppConfig.appName)
            .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_ALL)
            .setSmallIcon(R.drawable.ic_push_notification)
            .setColor(accentColor)
            .apply {
                val contentIntent = getMainIntent(context)
                contentIntent.flags =
                    Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                contentIntent.data = createIgnoredUri(inviteNotifiableEvent.eventId)
                setContentIntent(
                    buildNotificationClickIntent(inviteNotifiableEvent.roomId)
                )
                priority = NotificationCompat.PRIORITY_DEFAULT
                setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                setLights(accentColor, 500, 500)
                setAutoCancel(true)
            }
            .build()
    }

    @SuppressLint("LaunchActivityFromNotification", "MissingPermission")
    fun displayDiagnosticNotification() {
        val testActionIntent = Intent(context, TestNotificationReceiver::class.java)
        testActionIntent.action = NotificationActionIds.diagnostic
        val testPendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            testActionIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationManager.notify(
            "DIAGNOSTIC",
            888,
            NotificationCompat.Builder(context, ROOM_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(CirclesAppConfig.appName)
                .setContentText(context.getString(R.string.settings_troubleshoot_test_push_notification_content))
                .setSmallIcon(R.drawable.ic_push_notification)
                .setLargeIcon(context.getBitmap(R.drawable.ic_notifications))
                .setColor(ContextCompat.getColor(context, R.color.blue))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setContentIntent(testPendingIntent)
                .build()
        )
    }

    private fun buildNotificationClickIntent(roomId: String): PendingIntent =
        PendingIntent.getActivity(
            context,
            1,
            MainActivity.getOpenRoomIntent(context, roomId),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    private fun getMainIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java)
    }

    private fun createIgnoredUri(path: String): Uri = Uri.parse("ignored://$path")
}


