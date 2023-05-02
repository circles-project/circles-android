package org.futo.circles.model

import android.content.Context
import org.futo.circles.extensions.isConnectedToWifi
import org.matrix.android.sdk.api.session.events.model.Content

data class MediaBackupSettingsData(
    val isBackupEnabled: Boolean,
    val backupOverWifi: Boolean,
    val compressBeforeSending: Boolean,
    val folders: List<String>
) {
    companion object {
        const val isBackupEnabledKey = "is_backup_enabled"
        const val backupOverWifiKey = "backup_over_wifi"
        const val compressBeforeSendingKey = "compress_before_sending"
        const val foldersKey = "folders"
    }

    fun toMap() = mapOf(
        isBackupEnabledKey to isBackupEnabled,
        backupOverWifiKey to backupOverWifi,
        compressBeforeSendingKey to compressBeforeSending,
        foldersKey to folders
    )

    fun shouldStartBackup(context: Context): Boolean {
        if (isBackupEnabled) {
            if (backupOverWifi) {
                if (context.isConnectedToWifi()) return true
            } else return true
        }
        return false
    }
}

@Suppress("UNCHECKED_CAST")
fun Content?.toMediaBackupSettingsData() = MediaBackupSettingsData(
    (this?.get(MediaBackupSettingsData.isBackupEnabledKey) as? Boolean) ?: false,
    (this?.get(MediaBackupSettingsData.backupOverWifiKey) as? Boolean) ?: false,
    (this?.get(MediaBackupSettingsData.compressBeforeSendingKey) as? Boolean) ?: false,
    (this?.get(MediaBackupSettingsData.foldersKey) as? List<String>) ?: emptyList()
)