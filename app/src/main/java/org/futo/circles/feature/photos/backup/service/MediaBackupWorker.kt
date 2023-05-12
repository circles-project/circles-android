package org.futo.circles.feature.photos.backup.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import org.futo.circles.feature.photos.backup.MediaBackupDataSource
import org.futo.circles.provider.MatrixSessionProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MediaBackupWorker(context: Context, params: WorkerParameters) :
    CoroutineWorker(context, params), KoinComponent {

    private val mediaBackupDataSource: MediaBackupDataSource by inject()

    override suspend fun doWork(): Result {
        try {
            val syncService = MatrixSessionProvider.currentSession?.syncService()
            syncService?.startAutomaticBackgroundSync(
                BACKGROUND_SYNC_TIMEOUT,
                BACKGROUND_SYNC_REPEAT_DELAY
            )
            mediaBackupDataSource.startMediaBackup()
            syncService?.stopAnyBackgroundSync()
        } catch (t: Throwable) {
            Result.failure()
        }
        return Result.success()
    }

    companion object {
        private const val BACKGROUND_SYNC_TIMEOUT = 60L
        private const val BACKGROUND_SYNC_REPEAT_DELAY = 5L
    }

}
