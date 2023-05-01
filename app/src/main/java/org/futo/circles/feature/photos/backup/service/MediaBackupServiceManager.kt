package org.futo.circles.feature.photos.backup.service

import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.IBinder
import org.futo.circles.extensions.isConnectedToWifi
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
        }
    }

    private fun clear() {
        mediaBackupService = null
        mediaBackupService = null
    }
}