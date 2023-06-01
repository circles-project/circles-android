package org.futo.circles.core.share

import android.net.Uri
import androidx.lifecycle.ViewModel
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.timeline.post.SendMessageDataSource

class BaseShareViewModel(
    private val sendMessageDataSource: SendMessageDataSource
) : ViewModel() {

    val saveResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun uploadToRooms(
        uri: Uri,
        selectedRoomsId: List<String>,
        mediaType: MediaType
    ) {
        launchBg {
            selectedRoomsId.forEach {
                sendMessageDataSource.sendMedia(it, uri, null, null, mediaType)
            }
            saveResultLiveData.postValue(Response.Success(Unit))
        }
    }
}