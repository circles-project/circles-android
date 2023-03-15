package org.futo.circles.feature.photos.backup

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.extensions.launchBg
import org.futo.circles.model.MediaFolderListItem

class MediaBackupViewModel(
    private val dataSource: MediaBackupDataSource
) : ViewModel() {

    val mediaFolderLiveData = MutableLiveData<List<MediaFolderListItem>>()

    fun getMediaFolders() {
        launchBg { mediaFolderLiveData.postValue(dataSource.getMediaFolders()) }
    }

    fun onFolderBackupCheckChanged(id: Long) {

    }

    fun saveBackupSettings() {

    }

}