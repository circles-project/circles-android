package com.futo.circles.feature.photos.preview

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.timeline.post.PostOptionsDataSource
import com.futo.circles.feature.timeline.post.share.ShareableContent

class GalleryImageViewModel(
    private val roomId: String,
    private val eventId: String,
    galleryImageDataSource: GalleryImageDataSource,
    private val postOptionsDataSource: PostOptionsDataSource
) : ViewModel() {

    val galleryImageLiveData = MutableLiveData(galleryImageDataSource.getImageItem())
    val shareLiveData = SingleEventLiveData<ShareableContent>()
    val downloadImageLiveData = SingleEventLiveData<Unit>()

    fun shareImage() {
        val content = galleryImageLiveData.value?.imageContent ?: return
        launchBg {
            shareLiveData.postValue(postOptionsDataSource.getShareableContent(content))
        }
    }

    fun removeImage() {
        postOptionsDataSource.removeMessage(roomId, eventId)
    }

    fun saveImage() {
        val imageContent = galleryImageLiveData.value?.imageContent ?: return
        launchBg {
            postOptionsDataSource.saveImage(imageContent)
            downloadImageLiveData.postValue(Unit)
        }
    }
}