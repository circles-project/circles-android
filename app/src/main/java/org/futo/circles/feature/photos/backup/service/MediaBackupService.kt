package org.futo.circles.feature.photos.backup.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.provider.MediaStore
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit


class MediaBackupService : Service() {

    private val job = Job()
    private val backupScope = CoroutineScope(Dispatchers.IO + job)
    private val sendMessageDataSource: SendMessageDataSource by inject()
    private val mediaBackupDataSource: MediaBackupDataSource by inject()
    private var isBackupRunning = false

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            if (isBackupRunning) return
            val path = uri?.path ?: return
            if (mediaBackupDataSource.needToBackup(path)) {

            }
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

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        backupScope.launch {
            isBackupRunning = true
            val media = mediaBackupDataSource.getAllMediasToBackup()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        contentResolver.unregisterContentObserver(contentObserver);
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        fun getIntent(context: Context) = Intent(context, MediaBackupService::class.java)

        private fun scheduleBackup(context: Context) {
            val backupRequest = OneTimeWorkRequestBuilder<MediaBackupWorker>()
                .setInitialDelay(1, TimeUnit.DAYS)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .setRequiresCharging(false)
                        .build()
                ).build()
            WorkManager.getInstance(context.applicationContext).enqueueUniqueWork(
                "media_backup",
                ExistingWorkPolicy.REPLACE,
                backupRequest
            )
        }
    }
}
