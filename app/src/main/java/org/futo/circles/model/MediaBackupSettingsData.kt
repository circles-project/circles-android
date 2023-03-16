package org.futo.circles.model

data class MediaBackupSettingsData(
    val isBackupEnabled: Boolean,
    val folders: List<String>
) {
    companion object {
        const val isBackupEnabledKey = "is_backup_enabled"
        const val foldersKey = "folders"
    }

    fun toMap() = mapOf(
        isBackupEnabledKey to isBackupEnabled,
        foldersKey to folders
    )
}