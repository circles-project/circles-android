package org.futo.circles.feature.share

import android.net.Uri
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.picker.MediaType
import org.futo.circles.extensions.Response
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource

class BaseShareViewModel(
    private val sendMessageDataSource: SendMessageDataSource
) : ViewModel() {

    val saveResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun uploadToRooms(
        uri: Uri,
        selectedRoomsId: List<String>,
        mediaType: MediaType
    ) {
        selectedRoomsId.forEach {
            sendMessageDataSource.sendMedia(it, uri, null, mediaType)
        }
        saveResultLiveData.postValue(Response.Success(Unit))
    }
}