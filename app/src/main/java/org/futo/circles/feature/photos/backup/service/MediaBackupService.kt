package org.futo.circles.feature.photos.backup.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.futo.circles.R
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.koin.android.ext.android.inject


class MediaBackupService : Service() {

    private val job = SupervisorJob()
    private val backupScope = CoroutineScope(Dispatchers.IO + job)
    private val mediaBackupDataSource: MediaBackupDataSource by inject()
    private var backupJob: Job? = null


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(MEDIA_BACKUP_NOTIFICATION_ID, notification)
        startBackup {
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun startBackup(onDone: (() -> Unit)? = null) {
        backupJob = backupScope.launch {
            mediaBackupDataSource.startMediaBackup()
            backupJob = null
            onDone?.invoke()
        }
    }

    private fun createNotification(): Notification {
        val id = MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) createChannel()
        return NotificationCompat.Builder(applicationContext, id)
            .setContentTitle(applicationContext.getString(R.string.notification_media_backup))
            .setTicker(applicationContext.getString(R.string.notification_media_backup))
            .setContentText(applicationContext.getString(R.string.uploading))
            .setSmallIcon(R.drawable.ic_push_notification)
            .build()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        NotificationManagerCompat.from(applicationContext).createNotificationChannel(
            NotificationChannel(
                MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID,
                applicationContext.getString(R.string.notification_media_backup),
                NotificationManager.IMPORTANCE_MIN
            ).apply {
                description = applicationContext.getString(R.string.notification_media_backup)
                setSound(null, null)
                setShowBadge(false)
            })
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MediaBackupService::class.java)
        private const val MEDIA_BACKUP_NOTIFICATION_ID = 35
        private const val MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID =
            "MEDIA_BACKUP_NOTIFICATION_CHANNEL_ID"
    }
}
