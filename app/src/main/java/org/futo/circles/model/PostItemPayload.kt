package org.futo.circles.model

import org.futo.circles.core.model.PostReadInfo
import org.matrix.android.sdk.api.session.room.send.SendState

class PostItemPayload(
    val sendState: SendState,
    val readInfo: PostReadInfo,
    val repliesCount: Int,
    val needToUpdateFullItem: Boolean
)