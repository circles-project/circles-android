package org.futo.circles.feature.photos.save

import android.content.Context
import org.futo.circles.core.picker.MediaType
import org.futo.circles.core.utils.FileUtils
import org.futo.circles.extensions.onBG
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.model.ImageContent
import org.futo.circles.model.PostContent
import org.futo.circles.model.SelectableRoomListItem
import org.futo.circles.model.VideoContent

class SavePostToGalleryDataSource(
    private val context: Context,
    private val sendMessageDataSource: SendMessageDataSource
) {

    suspend fun saveMediaToGalleries(
        content: PostContent,
        selectedGalleries: List<SelectableRoomListItem>
    ) {
        var mediaType = MediaType.Image
        onBG {
            val uri = when (content) {
                is ImageContent -> {
                    mediaType = MediaType.Image
                    FileUtils.downloadEncryptedFileToContentUri(context, content.mediaContentData)
                }
                is VideoContent -> {
                    mediaType = MediaType.Video
                    FileUtils.downloadEncryptedFileToContentUri(context, content.mediaContentData)
                }
                else -> null
            }
            uri?.let {
                selectedGalleries.forEach {
                    sendMessageDataSource.sendMedia(it.id, uri, null, mediaType)
                }
            }
        }
    }
}