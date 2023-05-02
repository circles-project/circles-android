package org.futo.circles.feature.photos.backup.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import org.futo.circles.extensions.isConnectedToWifi

class MediaBackupWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        if (applicationContext.isConnectedToWifi()) {
            backupMediaFiles()
        } else {
            return Result.failure()
        }

        return Result.success()
    }

    private fun backupMediaFiles() {
        
    }
}
