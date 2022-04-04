package com.futo.circles.core.matrix

import android.net.Uri
import com.futo.circles.core.CIRCLES_SPACE_TYPE
import com.futo.circles.core.CIRCLE_TYPE
import org.matrix.android.sdk.api.session.space.CreateSpaceParams

class CreateSpaceDataSource : CreateRoomDataSource() {

    suspend fun createCircle(
        name: String,
        iconUri: Uri? = null
    ) = createSpace(CIRCLE_TYPE, name, iconUri, CIRCLES_SPACE_TYPE)


    suspend fun createSpace(
        type: String,
        name: String,
        iconUri: Uri? = null,
        parentSpaceType: String? = null
    ): String {
        val params = CreateSpaceParams().apply {
            this.name = name
            avatarUri = iconUri
            roomType = type
        }
        val spaceId = session?.spaceService()?.createSpace(params) ?: return ""
        parentSpaceType?.let { setRelations(spaceId, it) }

        return spaceId
    }
}