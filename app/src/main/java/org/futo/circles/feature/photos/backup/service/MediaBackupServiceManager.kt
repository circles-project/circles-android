package org.futo.circles.feature.photos.backup.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import org.futo.circles.model.MediaBackupSettingsData
import java.util.concurrent.TimeUnit


class MediaBackupServiceManager {

    private var mediaBackupService: MediaBackupService? = null
    private var savedBackupSettings: MediaBackupSettingsData? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as MediaBackupService.MediaBackupServiceBinder
            mediaBackupService = binder.getService()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            clear()
        }
    }

    fun bindMediaServiceIfNeeded(context: Context, backupSettingsData: MediaBackupSettingsData) {
        if (savedBackupSettings == backupSettingsData) return
        savedBackupSettings = backupSettingsData
        mediaBackupService?.onBackupSettingsUpdated() ?: run {
            MediaBackupService.bindService(context, connection)
            //scheduleBackup(context)
        }
    }

    fun unbindMediaService(context: Context) {
        try {
            context.unbindService(connection)
            clear()
        } catch (_: Exception) {
        }
    }

    private fun clear() {
        mediaBackupService = null
        mediaBackupService = null
    }

    private fun scheduleBackup(context: Context) {
        val backupRequest =
            OneTimeWorkRequestBuilder<MediaBackupWorker>()
                .setInitialDelay(10, TimeUnit.SECONDS)
                .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            MEDIA_BACKUP_SCHEDULED_WORK_KEY,
            ExistingWorkPolicy.REPLACE,
            backupRequest
        )
        Log.d("MyLog", "scheduled")
    }

    companion object {
        private const val MEDIA_BACKUP_SCHEDULED_WORK_KEY = "media_backup"
    }
}