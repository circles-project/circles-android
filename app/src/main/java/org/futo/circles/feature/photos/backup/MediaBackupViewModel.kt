package org.futo.circles.feature.photos.backup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.room.RoomAccountDataSource
import org.futo.circles.model.MediaBackupSettingsData
import org.futo.circles.model.MediaFolderListItem

class MediaBackupViewModel(
    private val roomAccountDataSource: RoomAccountDataSource,
    private val mediaBackupDataSource: MediaBackupDataSource
) : ViewModel() {

    val mediaFolderLiveData = SingleEventLiveData<List<MediaFolderListItem>>()
    val saveBackupSettingsResultLiveData = SingleEventLiveData<Response<Unit?>>()
    val initialBackupSettingsLiveData = SingleEventLiveData<MediaBackupSettingsData>()
    val isSettingsDataChangedLiveData = MutableLiveData(false)
    private val selectedFoldersIds = mutableSetOf<String>()

    init {
        getInitialBackupSettings()
    }

    private fun getInitialBackupSettings() {
        val data = roomAccountDataSource.getMediaBackupSettings()
        initialBackupSettingsLiveData.value = data
        selectedFoldersIds.addAll(data.folders)
        launchBg {
            mediaFolderLiveData.postValue(
                mediaBackupDataSource.getAllMediaFolders(selectedFoldersIds.toList())
            )
        }
    }

    fun onFolderBackupCheckChanged(
        id: String,
        isSelected: Boolean,
        isBackupEnabled: Boolean,
        backupOverWifi: Boolean,
        compressBeforeSending: Boolean
    ) {
        if (isSelected) selectedFoldersIds.add(id)
        else selectedFoldersIds.remove(id)
        handleDataSettingsChanged(isBackupEnabled, backupOverWifi, compressBeforeSending)
    }

    fun saveBackupSettings(
        isBackupEnabled: Boolean,
        backupOverWifi: Boolean,
        compressBeforeSending: Boolean
    ) {
        launchBg {
            val result = roomAccountDataSource.saveMediaBackupSettings(
                MediaBackupSettingsData(
                    isBackupEnabled,
                    backupOverWifi,
                    compressBeforeSending,
                    selectedFoldersIds.toList()
                )
            )
            saveBackupSettingsResultLiveData.postValue(result)
        }
    }

    fun handleDataSettingsChanged(
        isBackupEnabled: Boolean,
        backupOverWifi: Boolean,
        compressBeforeSending: Boolean
    ) {
        val newSettings =
            MediaBackupSettingsData(
                isBackupEnabled,
                backupOverWifi,
                compressBeforeSending,
                selectedFoldersIds.toList()
            )
        isSettingsDataChangedLiveData.value = newSettings != initialBackupSettingsLiveData.value
    }

}