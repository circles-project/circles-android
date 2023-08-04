package org.futo.circles.model

import org.matrix.android.sdk.api.session.room.send.SendState

class PostItemPayload(
    val sendState: SendState,
    val readByCount: Int,
    val repliesCount: Int,
    val needToUpdateFullItem: Boolean
)