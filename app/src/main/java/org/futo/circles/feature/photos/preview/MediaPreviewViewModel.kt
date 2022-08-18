package org.futo.circles.feature.photos.preview

import android.content.Context
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.utils.FileUtils
import org.futo.circles.extensions.launchBg
import org.futo.circles.feature.timeline.post.PostOptionsDataSource
import org.futo.circles.feature.share.ShareableContent
import org.futo.circles.model.ImageContent
import org.futo.circles.model.VideoContent

class MediaPreviewViewModel(
    private val roomId: String,
    private val eventId: String,
    private val mediaPreviewDataSource: MediaPreviewDataSource,
    private val postOptionsDataSource: PostOptionsDataSource
) : ViewModel() {

    val imageLiveData = MutableLiveData<ImageContent>()
    val videoLiveData = MutableLiveData<Pair<VideoContent, Uri>>()
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadLiveData = SingleEventLiveData<Unit>()


    fun loadData(context: Context) {
        val content = mediaPreviewDataSource.getPostContent() ?: return
        when (content) {
            is ImageContent -> imageLiveData.postValue(content)
            is VideoContent -> launchBg {
                val uri =
                    FileUtils.downloadEncryptedFileToContentUri(context, content.mediaContentData)
                uri?.let { videoLiveData.postValue(content to uri) }
            }
            else -> throw IllegalArgumentException("Wrong media type")
        }
    }

    fun share() {
        val content = mediaPreviewDataSource.getPostContent() ?: return
        launchBg {
            shareLiveData.postValue(postOptionsDataSource.getShareableContent(content))
        }
    }

    fun removeImage() {
        postOptionsDataSource.removeMessage(roomId, eventId)
    }

    fun save() {
        val content = mediaPreviewDataSource.getPostContent() ?: return
        launchBg {
            postOptionsDataSource.saveMediaToDevice(content)
            downloadLiveData.postValue(Unit)
        }
    }
}