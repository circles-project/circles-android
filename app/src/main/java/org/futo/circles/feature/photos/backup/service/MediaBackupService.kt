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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.getRoomIdByTag
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.model.Gallery
import org.koin.android.ext.android.inject


class MediaBackupService : Service() {

    private val job = Job()
    private val backupScope = CoroutineScope(Dispatchers.IO + job)
    private val sendMessageDataSource: SendMessageDataSource by inject()
    private val createRoomDataSource: CreateRoomDataSource by inject()
    private val mediaBackupDataSource: MediaBackupDataSource by inject()
    private var isBackupRunning = false

    private val contentObserver = object : ContentObserver(Handler(Looper.getMainLooper())) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            if (isBackupRunning) return
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
            createGalleriesIfNotExist(media.keys.toList())
            media.keys.forEach { bucketId ->
                val roomId = getRoomIdByTag(bucketId) ?: return@forEach
                media[bucketId]?.forEach { item ->
                    sendMessageDataSource.sendMedia(
                        roomId, item.uri, null, null, MediaType.Image
                    )
                }
            }
            isBackupRunning = false
        }
        return START_STICKY
    }

    private suspend fun createGalleriesIfNotExist(folderKeys: List<String>) {
        folderKeys.forEach { bucketId ->
            if (getRoomIdByTag(bucketId) == null) {
                createRoomDataSource.createRoom(
                    circlesRoom = Gallery(tag = bucketId),
                    name = mediaBackupDataSource.getFolderNameBy(bucketId)
                )
            }
        }
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
