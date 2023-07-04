package org.futo.circles.gallery.feature.backup.service

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import org.futo.circles.core.provider.MatrixSessionProvider
import org.futo.circles.gallery.feature.backup.MediaBackupDataSource
import javax.inject.Inject

@HiltWorker
class MediaBackupWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    @Inject
    lateinit var mediaBackupDataSource: MediaBackupDataSource

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
