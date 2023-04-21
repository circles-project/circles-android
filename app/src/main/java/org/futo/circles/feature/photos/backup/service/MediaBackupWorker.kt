package org.futo.circles.feature.photos.backup.service

import android.content.Context
import android.net.ConnectivityManager
import androidx.work.Worker
import androidx.work.WorkerParameters

class MediaBackupWorker(context: Context, params: WorkerParameters) : Worker(context, params) {

    override fun doWork(): Result {
        val connectivityManager = applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected && networkInfo.type == ConnectivityManager.TYPE_WIFI) {
            backupMediaFiles()
        } else {

            return Result.failure()
        }

        return Result.success()
    }

    private fun backupMediaFiles() {

    }
}
