package com.futo.circles.feature.photos.gallery

import android.net.Uri
import com.futo.circles.core.SingleEventLiveData
import com.futo.circles.extensions.Response
import com.futo.circles.extensions.launchBg
import com.futo.circles.feature.room.LeaveRoomDataSource
import com.futo.circles.feature.timeline.BaseTimelineViewModel
import com.futo.circles.feature.timeline.data_source.TimelineDataSource

class GalleryViewModel(
    private val roomId: String,
    private val timelineDataSource: TimelineDataSource,
    private val leaveRoomDataSource: LeaveRoomDataSource
) : BaseTimelineViewModel(timelineDataSource) {

    val scrollToTopLiveData = SingleEventLiveData<Unit>()
    val deleteGalleryLiveData = SingleEventLiveData<Response<Unit?>>()

    fun uploadImage(uri: Uri) {
        timelineDataSource.sendImage(roomId, uri, null)
        scrollToTopLiveData.postValue(Unit)
    }

    fun deleteGallery() {
        launchBg { deleteGalleryLiveData.postValue(leaveRoomDataSource.deleteGallery()) }
    }
}