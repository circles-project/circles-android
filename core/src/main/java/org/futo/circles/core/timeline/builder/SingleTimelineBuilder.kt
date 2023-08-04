package org.futo.circles.core.timeline.builder

import org.futo.circles.core.mapping.toPost
import org.futo.circles.core.model.Post
import org.matrix.android.sdk.api.session.room.timeline.TimelineEvent
import javax.inject.Inject

class SingleTimelineBuilder @Inject constructor() : BaseTimelineBuilder() {

    override fun List<TimelineEvent>.processSnapshot(isThread: Boolean): List<Post> =
        sortList(this.map { it.toPost() }, isThread)

}