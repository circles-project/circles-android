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
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.koin.android.ext.android.inject


class MediaBackupService : Service() {

    private val job = Job()
    private val backupScope = CoroutineScope(Dispatchers.IO + job)
    private val mediaBackupDataSource: MediaBackupDataSource by inject()
    private var isBackupRunning = false

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            if (isBackupRunning || selfChange) return
            val path = uri?.path ?: return
            Log.d("MyLog", "from observer $uri")
            backupScope.launch { mediaBackupDataSource.startBackupByFilePath(path) }
        }
    }

    override fun onCreate() {
        super.onCreate()
        contentResolver.registerContentObserver(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("MyLog", "start")
        backupScope.launch {
            isBackupRunning = true
            mediaBackupDataSource.startMediaBackup()
            isBackupRunning = false
        }
        return START_NOT_STICKY
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
    }
}
