package org.futo.circles.model

import org.futo.circles.core.model.ReactionsData
import org.matrix.android.sdk.api.session.room.send.SendState

class PostItemPayload(
    val readByCount: Int,
    val repliesCount: Int,
    val reactions: List<ReactionsData>,
    val needToUpdateFullItem: Boolean
)