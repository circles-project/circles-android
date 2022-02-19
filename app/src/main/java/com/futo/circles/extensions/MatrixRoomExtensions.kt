package com.futo.circles.extensions

import org.matrix.android.sdk.api.session.room.model.RoomSummary

fun List<RoomSummary>.containsTag(tagName: String) = filter { room ->
    room.tags.firstOrNull { tag -> tag.name.contains(tagName) }?.let { true } ?: false
}
