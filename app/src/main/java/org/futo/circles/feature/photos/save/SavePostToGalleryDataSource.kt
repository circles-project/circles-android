package org.futo.circles.feature.photos.save

import android.content.Context
import org.futo.circles.extensions.onBG
import org.futo.circles.feature.timeline.data_source.SendMessageDataSource
import org.futo.circles.model.MediaContent
import org.futo.circles.model.PostContent
import org.futo.circles.model.SelectableRoomListItem

class SavePostToGalleryDataSource(
    private val context: Context,
    private val sendMessageDataSource: SendMessageDataSource
) {

    suspend fun saveMediaToGalleries(
        content: PostContent,
        selectedGalleries: List<SelectableRoomListItem>
    ) {
        val mediaContent = content as? MediaContent ?: return
        onBG {
            val uri =
                FileUtils.downloadEncryptedFileToContentUri(context, mediaContent.mediaFileData)
            uri?.let {
                selectedGalleries.forEach {
                    sendMessageDataSource.sendMedia(
                        it.id, uri, null, null, mediaContent.getMediaType()
                    )
                }
            }
        }
    }
}