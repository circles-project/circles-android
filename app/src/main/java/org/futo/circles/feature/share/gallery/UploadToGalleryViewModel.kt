package org.futo.circles.feature.share.gallery

import android.net.Uri
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.picker.MediaType
import org.futo.circles.extensions.Response
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.model.SelectableRoomListItem

class UploadToGalleryViewModel(
    private val sendMessageDataSource: SendMessageDataSource
) : ViewModel() {

    val saveResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun uploadToGalleries(uri: Uri, selectedGalleries: List<SelectableRoomListItem>,mediaType: MediaType) {
        selectedGalleries.forEach {
            sendMessageDataSource.sendMedia(it.id, uri, null,mediaType)
        }
        saveResultLiveData.postValue(Response.Success(Unit))
    }
}