package org.futo.circles.core.model

import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent

data class OtherEventContent(
    val eventType: String,
) : PostContent(PostContentType.OTHER_CONTENT)

fun TimelineEvent.toOtherEventContent(): OtherEventContent = OtherEventContent(root.getClearType())