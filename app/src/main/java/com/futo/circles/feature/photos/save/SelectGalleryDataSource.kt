package com.futo.circles.feature.photos.save

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.futo.circles.extensions.getUri
import com.futo.circles.extensions.onBG
import com.futo.circles.feature.timeline.data_source.SendMessageDataSource
import com.futo.circles.mapping.toSelectableRoomListItem
import com.futo.circles.model.GALLERY_TYPE
import com.futo.circles.model.ImageContent
import com.futo.circles.model.SelectableRoomListItem
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.room.model.Membership
import org.matrix.android.sdk.api.session.room.roomSummaryQueryParams

class SelectGalleryDataSource(
    private val context: Context,
    private val sendMessageDataSource: SendMessageDataSource
) {

    private val session by lazy { MatrixSessionProvider.currentSession }

    val galleriesLiveData = MutableLiveData(getInitialGalleriesList())

    private fun getInitialGalleriesList(): List<SelectableRoomListItem> =
        session?.roomService()?.getRoomSummaries(roomSummaryQueryParams {
            excludeType = null
        })?.mapNotNull { summary ->
            if (summary.roomType == GALLERY_TYPE && summary.membership == Membership.JOIN)
                summary.toSelectableRoomListItem()
            else null
        } ?: emptyList()

    private fun getSelectedGalleries() =
        galleriesLiveData.value?.filter { it.isSelected } ?: emptyList()

    fun toggleGallerySelect(gallery: SelectableRoomListItem) {
        val newList = galleriesLiveData.value?.toMutableList()?.map {
            if (it.id == gallery.id) it.copy(isSelected = !it.isSelected) else it
        }
        galleriesLiveData.postValue(newList)
    }

    suspend fun saveImageToGalleries(imageContent: ImageContent) {
        onBG {
            val uri = Glide.with(context).asFile().load(imageContent).submit().get().getUri(context)
            getSelectedGalleries().forEach {
                sendMessageDataSource.sendImage(it.id, uri, null)
            }
        }
    }
}