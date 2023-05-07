package org.futo.circles.feature.photos.backup.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import org.futo.circles.R
import java.util.UUID

object MediaBackupWorkerNotificationHelper {

    private const val MEDIA_BACKUP_NOTIFICATION_ID = 35
    private const val MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID =
        "MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID"

    fun createForegroundInfo(context: Context, workId: UUID): ForegroundInfo {
        val id = MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID
        val cancelIntent =
            WorkManager.getInstance(context).createCancelPendingIntent(workId)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel(context)

        val notification = NotificationCompat.Builder(context, id)
            .setContentTitle(context.getString(R.string.notification_media_backup))
            .setTicker(context.getString(R.string.notification_media_backup))
            .setContentText(context.getString(R.string.uploading))
            .setSmallIcon(R.drawable.ic_push_notification)
            .setOngoing(true)
            .addAction(
                R.drawable.ic_close,
                context.getString(R.string.cancel),
                cancelIntent
            )
            .build()

        return ForegroundInfo(MEDIA_BACKUP_NOTIFICATION_ID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel(context: Context) {
        NotificationManagerCompat.from(context).createNotificationChannel(
            NotificationChannel(
                MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID,
                context.getString(R.string.notification_media_backup),
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = context.getString(R.string.notification_media_backup)
                setSound(null, null)
                setShowBadge(false)
            })
    }
}