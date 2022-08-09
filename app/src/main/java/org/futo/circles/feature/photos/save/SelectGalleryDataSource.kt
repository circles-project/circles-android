package org.futo.circles.feature.photos.save

import android.content.Context
import androidx.lifecycle.MutableLiveData
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.FileUtils.downloadEncryptedFileToContentUri
import org.futo.circles.extensions.onBG
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.mapping.toSelectableRoomListItem
import org.futo.circles.model.*
import org.futo.circles.provider.MatrixSessionProvider
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

    suspend fun saveMediaToGalleries(content: PostContent) {
        var mediaType = MediaType.Image
        onBG {
            val uri = when (content) {
                is ImageContent -> {
                    mediaType = MediaType.Image
                    downloadEncryptedFileToContentUri(context, content.mediaContentData)
                }
                is VideoContent -> {
                    mediaType = MediaType.Video
                    downloadEncryptedFileToContentUri(context, content.mediaContentData)
                }
                else -> null
            }
            uri?.let {
                getSelectedGalleries().forEach {
                    sendMessageDataSource.sendMedia(it.id, uri, null, mediaType)
                }
            }
        }
    }
}