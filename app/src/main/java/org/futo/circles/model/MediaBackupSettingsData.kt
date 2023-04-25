package org.futo.circles.model

import org.matrix.android.sdk.api.session.events.model.Content

data class MediaBackupSettingsData(
    val isBackupEnabled: Boolean,
    val backupOverWifi: Boolean,
    val folders: List<String>
) {
    companion object {
        const val isBackupEnabledKey = "is_backup_enabled"
        const val backupOverWifiKey = "backup_over_wifi"
        const val foldersKey = "folders"
    }

    fun toMap() = mapOf(
        isBackupEnabledKey to isBackupEnabled,
        backupOverWifiKey to backupOverWifi,
        foldersKey to folders
    )
}

@Suppress("UNCHECKED_CAST")
fun Content?.toMediaBackupSettingsData() = MediaBackupSettingsData(
    (this?.get(MediaBackupSettingsData.isBackupEnabledKey) as? Boolean) ?: false,
    (this?.get(MediaBackupSettingsData.backupOverWifiKey) as? Boolean) ?: false,
    (this?.get(MediaBackupSettingsData.foldersKey) as? List<String>) ?: emptyList()
)