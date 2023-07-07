package org.futo.circles.gallery.feature.gallery.full_screen.media_item

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.MediaContent
import org.futo.circles.core.model.PostContentType
import org.futo.circles.core.utils.FileUtils
import javax.inject.Inject

@HiltViewModel
class FullScreenMediaViewModel @Inject constructor(
    private val fullScreenMediaDataSource: FullScreenMediaDataSource
) : ViewModel() {

    val imageLiveData = MutableLiveData<MediaContent>()
    val videoLiveData = MutableLiveData<Pair<MediaContent, Uri>>()

    fun loadData(context: Context) {
        val content = (fullScreenMediaDataSource.getPostContent() as? MediaContent) ?: return
        when (content.type) {
            PostContentType.IMAGE_CONTENT -> imageLiveData.postValue(content)
            PostContentType.VIDEO_CONTENT -> launchBg {
                FileUtils.downloadEncryptedFileToContentUri(context, content.mediaFileData)
                    ?.let { videoLiveData.postValue(content to it) }
            }

            else -> return
        }
    }
}