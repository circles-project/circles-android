package org.futo.circles.feature.photos.preview

import org.futo.circles.mapping.toPost
import org.futo.circles.model.GalleryImageListItem
import org.futo.circles.model.ImageContent
import org.futo.circles.model.PostContentType
import org.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.getRoom
import org.matrix.android.sdk.api.session.room.getTimelineEvent

class GalleryImageDataSource(
    private val roomId: String,
    private val eventId: String
) {

    private val session = MatrixSessionProvider.currentSession

    fun getImageItem(): GalleryImageListItem? {
        val roomForMessage = session?.getRoom(roomId)
        val post = roomForMessage?.getTimelineEvent(eventId)?.toPost(PostContentType.IMAGE_CONTENT)
        return (post?.content as? ImageContent)?.let {
            GalleryImageListItem(post.id, it, post.postInfo)
        }
    }


}