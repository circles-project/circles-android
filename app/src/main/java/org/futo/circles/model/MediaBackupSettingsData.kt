package org.futo.circles.model

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