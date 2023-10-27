package org.futo.circles.core.model

enum class CircleRoomTypeArg { Circle, Group, Photo }

fun CircleRoomTypeArg.toShareUrlType() = when (this) {
    CircleRoomTypeArg.Circle -> ShareUrlTypeArg.TIMELINE
    CircleRoomTypeArg.Group -> ShareUrlTypeArg.GROUP
    CircleRoomTypeArg.Photo -> ShareUrlTypeArg.GALLERY
}