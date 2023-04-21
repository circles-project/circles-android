package org.futo.circles.feature.photos.backup.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.koin.android.ext.android.inject
import java.util.*


class MediaBackupService : Service() {

    private val sendMessageDataSource: SendMessageDataSource by inject()
    private val mediaBackupDataSource: MediaBackupDataSource by inject()

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {

        }
    }

    override fun onCreate() {
        super.onCreate()
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, contentObserver
        )
        contentResolver.registerContentObserver(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, false, contentObserver
        )
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        contentResolver.unregisterContentObserver(contentObserver);
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MediaBackupService::class.java)

        fun scheduleBackup(context: Context) {
            val pendingIntent =
                PendingIntent.getService(
                    context,
                    0,
                    getIntent(context),
                    PendingIntent.FLAG_IMMUTABLE
                )
            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                add(Calendar.DAY_OF_YEAR, 1)
            }
            (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntent
            )
        }
    }
}
