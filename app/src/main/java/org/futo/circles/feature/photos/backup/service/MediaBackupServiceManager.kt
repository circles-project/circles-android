package org.futo.circles.feature.photos.backup.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import org.futo.circles.model.MediaBackupSettingsData


class MediaBackupServiceManager {

    private var savedBackupSettings: MediaBackupSettingsData? = null

    fun bindMediaServiceIfNeeded(context: Context, backupSettingsData: MediaBackupSettingsData) {
        if (savedBackupSettings == backupSettingsData) return
        savedBackupSettings = backupSettingsData
        context.startService(MediaBackupService.getIntent(context))
        scheduleBackup(context)
    }

    private fun scheduleBackup(context: Context) {
        val intent = Intent(context, MediaBackupBroadcastReceiver::class.java)
        val pendingIntent =
            PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        val alarmManager =
            (context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager) ?: return
        val interval = AlarmManager.INTERVAL_FIFTEEN_MINUTES
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            System.currentTimeMillis() + interval,
            interval,
            pendingIntent
        )
    }
}