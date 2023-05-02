package org.futo.circles.feature.photos.backup.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import android.provider.MediaStore
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.futo.circles.extensions.isConnectedToWifi
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
        if (backupSettingsData.isBackupEnabled) {
            if (backupSettingsData.backupOverWifi) {
                if (context.isConnectedToWifi()) bindMediaService(context, backupSettingsData)
            } else bindMediaService(context, backupSettingsData)
        } else {
            unbindMediaService(context)
        }
    }

    fun unbindMediaService(context: Context) {
        context.unbindService(connection)
        clear()
    }

    private fun bindMediaService(context: Context, backupSettingsData: MediaBackupSettingsData) {
        savedBackupSettings = backupSettingsData
        mediaBackupService?.onBackupSettingsUpdated() ?: run {
            MediaBackupService.getIntent(context).also { intent ->
                context.bindService(intent, connection, Context.BIND_AUTO_CREATE)
            }
            scheduleBackup(context)
        }
    }

    private fun clear() {
        mediaBackupService = null
        mediaBackupService = null
    }

    private fun scheduleBackup(context: Context) {
        val backupRequest = PeriodicWorkRequestBuilder<MediaBackupWorker>(1, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .addContentUriTrigger(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true)
                    .build()
            ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MEDIA_BACKUP_SCHEDULED_WORK_KEY,
            ExistingPeriodicWorkPolicy.UPDATE,
            backupRequest
        )
    }

    companion object {
        private const val MEDIA_BACKUP_SCHEDULED_WORK_KEY = "media_backup"
    }
}