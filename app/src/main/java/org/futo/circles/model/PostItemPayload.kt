package org.futo.circles.model

class PostItemPayload(
    val repliesCount: Int,
    val isRepliesVisible: Boolean,
    val hasReplies: Boolean,
    val needToUpdateFullItem: Boolean
)