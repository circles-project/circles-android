package org.futo.circles.feature.photos.backup.service

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.core.matrix.room.CreateRoomDataSource
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.futo.circles.feature.room.RoomAccountDataSource
import org.futo.circles.model.Gallery
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MediaBackupWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val mediaBackupDataSource: MediaBackupDataSource by inject()
    private val createRooDataSource: CreateRoomDataSource by inject()
    private val roomAccountDataSource: RoomAccountDataSource by inject()

    override suspend fun doWork(): Result {
        withContext(Dispatchers.IO) {
//            val ses = MatrixSessionProvider.currentSession ?: return@withContext
//            Log.d("MyLog", ses.toString())
//            MatrixSessionProvider.awaitForSessionSync(ses)
//            Log.d("MyLog", "finish await")
            createRooDataSource.createRoom(
                circlesRoom = Gallery(tag = "123"),
                name = "Test"
            )
        }
        return Result.success()
    }

    private suspend fun backupMediaFiles() {
        val backupSettings = roomAccountDataSource.getMediaBackupSettings()
        Log.d("MyLog", backupSettings.toString())
        if (backupSettings.shouldStartBackup(applicationContext))
            mediaBackupDataSource.startMediaBackup()
    }
}
