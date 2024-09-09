package org.futo.circles.model

import org.futo.circles.core.model.ReactionsData

class DmTimelineItemPayload(
    val reactions: List<ReactionsData>,
    val needToUpdateFullItem: Boolean
)