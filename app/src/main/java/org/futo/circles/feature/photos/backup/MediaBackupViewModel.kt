package org.futo.circles.feature.photos.backup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.extensions.Response
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.MediaBackupSettingsData
import org.futo.circles.model.MediaFolderListItem

class MediaBackupViewModel(
    private val dataSource: MediaBackupDataSource
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
        val data = dataSource.getInitialBackupSettings()
        initialBackupSettingsLiveData.value = data
        selectedFoldersIds.addAll(data.folders)
        launchBg { mediaFolderLiveData.postValue(dataSource.getMediaFolders(selectedFoldersIds.toList())) }
    }

    fun onFolderBackupCheckChanged(id: String, isBackupEnabled: Boolean) {
        if (selectedFoldersIds.contains(id)) selectedFoldersIds.remove(id)
        else selectedFoldersIds.add(id)
        handleDataSettingsChanged(isBackupEnabled)
    }

    fun saveBackupSettings(isBackupEnabled: Boolean) {
        launchBg {
            val result = dataSource.saveBackupSettings(
                MediaBackupSettingsData(isBackupEnabled, selectedFoldersIds.toList())
            )
            saveBackupSettingsResultLiveData.postValue(result)
        }
    }

    fun handleDataSettingsChanged(isBackupEnabled: Boolean) {
        val newSettings = MediaBackupSettingsData(isBackupEnabled, selectedFoldersIds.toList())
        isSettingsDataChangedLiveData.value = newSettings != initialBackupSettingsLiveData.value
    }

}