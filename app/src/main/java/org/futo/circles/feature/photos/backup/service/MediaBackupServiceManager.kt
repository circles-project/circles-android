package org.futo.circles.feature.photos.backup.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import org.futo.circles.model.MediaBackupSettingsData


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
        bindMediaService(context, backupSettingsData)
    }

    fun unbindMediaService(context: Context) {
        try {
            context.unbindService(connection)
            clear()
        } catch (_: Exception) {
        }
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
        val intent = Intent(context, MediaBackupBroadcastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager =
            (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager) ?: return
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis(),
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    }
}