package org.futo.circles.feature.notifications

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
import androidx.core.os.bundleOf
import androidx.navigation.NavDeepLinkBuilder
import org.futo.circles.MainActivity
import org.futo.circles.R
import org.futo.circles.extensions.getBitmap
import org.futo.circles.feature.notifications.test.task.TestNotificationReceiver
import org.futo.circles.model.CircleRoomTypeArg
import org.futo.circles.model.RoomEventGroupInfo

class NotificationUtils(
    private val context: Context
) {

    companion object {
        const val NOTIFICATION_ID_FOREGROUND_SERVICE = 61
        private const val LISTENING_FOR_EVENTS_NOTIFICATION_CHANNEL_ID =
            "LISTEN_FOR_EVENTS_NOTIFICATION_CHANNEL_ID"
        private const val NOISY_NOTIFICATION_CHANNEL_ID = "DEFAULT_NOISY_NOTIFICATION_CHANNEL_ID"

        @ChecksSdkIntAtLeast(api = Build.VERSION_CODES.O)
        fun supportNotificationChannels() = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    }

    private val notificationManager = NotificationManagerCompat.from(context)


    fun createNotificationChannels() {
        if (!supportNotificationChannels()) return

        val accentColor = ContextCompat.getColor(context, R.color.launcher_background_color)

        notificationManager.createNotificationChannel(NotificationChannel(
            NOISY_NOTIFICATION_CHANNEL_ID,
            context.getString(R.string.notification_noisy_notifications),
            NotificationManager.IMPORTANCE_DEFAULT
        )
            .apply {
                description = context.getString(R.string.notification_noisy_notifications)
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
        // build the pending intent go to the home screen if this is clicked.
        val i = getMainIntent(context)
        i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val mainIntent = getMainIntent(context)
        val pi =
            PendingIntent.getActivity(context, 0, mainIntent, PendingIntent.FLAG_IMMUTABLE)

        val accentColor = ContextCompat.getColor(context, R.color.launcher_background_color)

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

    /**
     * Build a notification for a Room.
     */
    fun buildMessagesListNotification(
        messageStyle: NotificationCompat.MessagingStyle,
        roomInfo: RoomEventGroupInfo,
        largeIcon: Bitmap?,
        lastMessageTimestamp: Long,
        tickerText: String
    ): Notification {
        val accentColor = ContextCompat.getColor(context, R.color.launcher_background_color)


        val channelID = NOISY_NOTIFICATION_CHANNEL_ID
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

                setContentIntent(buildOpenRoomIntent(roomInfo.roomId))
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
            NotificationCompat.Builder(context, NOISY_NOTIFICATION_CHANNEL_ID)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.settings_troubleshoot_test_push_notification_content))
                .setSmallIcon(R.drawable.ic_push_notification)
                .setLargeIcon(context.getBitmap(R.mipmap.ic_launcher))
                .setColor(ContextCompat.getColor(context, R.color.launcher_background_color))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setCategory(NotificationCompat.CATEGORY_STATUS)
                .setAutoCancel(true)
                .setContentIntent(testPendingIntent)
                .build()
        )
    }

    private fun buildOpenRoomIntent(roomId: String): PendingIntent =
        NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph_bottom_menu)
            .setDestination(R.id.timelineFragment)
            .setArguments(bundleOf("roomId" to roomId, "type" to CircleRoomTypeArg.Group))
            .createPendingIntent()
}

fun getMainIntent(context: Context): Intent {
    return Intent(context, MainActivity::class.java)
}

const val IGNORED_SCHEMA = "ignored"

fun createIgnoredUri(path: String): Uri = Uri.parse("$IGNORED_SCHEMA://$path")
