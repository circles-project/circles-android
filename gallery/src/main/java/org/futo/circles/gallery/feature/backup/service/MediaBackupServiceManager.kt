package org.futo.circles.gallery.feature.backup.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import org.futo.circles.core.CirclesAppConfig
import org.futo.circles.gallery.model.MediaBackupSettingsData
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaBackupServiceManager @Inject constructor(){

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
        if (CirclesAppConfig.isMediaBackupEnabled.not()) return
        if (savedBackupSettings == backupSettingsData) return
        savedBackupSettings = backupSettingsData
        mediaBackupService?.onBackupSettingsUpdated() ?: run {
            MediaBackupService.bindService(context, connection)
            scheduleBackup(context)
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
            PeriodicWorkRequestBuilder<MediaBackupWorker>(
                REPEAT_INTERVAL_HOURS,
                TimeUnit.HOURS,
                FLEX_INTERVAL_MINUTES,
                TimeUnit.MINUTES
            ).setConstraints(
                Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
            ).build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            MEDIA_BACKUP_SCHEDULED_WORK_KEY,
            ExistingPeriodicWorkPolicy.UPDATE,
            backupRequest
        )
    }

    companion object {
        private const val MEDIA_BACKUP_SCHEDULED_WORK_KEY = "media_backup"
        private const val REPEAT_INTERVAL_HOURS = 6L
        private const val FLEX_INTERVAL_MINUTES = 30L
    }
}