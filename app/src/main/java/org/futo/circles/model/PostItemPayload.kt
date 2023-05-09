package org.futo.circles.model

import org.matrix.android.sdk.api.session.room.send.SendState

class PostItemPayload(
    val sendState: SendState,
    val readInfo: PostReadInfo,
    val needToUpdateFullItem: Boolean
)