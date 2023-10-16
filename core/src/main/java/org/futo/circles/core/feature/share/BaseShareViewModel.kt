package org.futo.circles.core.feature.share

import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.MediaType
import org.futo.circles.core.feature.timeline.post.SendMessageDataSource
import javax.inject.Inject

@HiltViewModel
class BaseShareViewModel @Inject constructor(
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