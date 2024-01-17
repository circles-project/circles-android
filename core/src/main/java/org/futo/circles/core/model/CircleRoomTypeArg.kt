package org.futo.circles.core.model

enum class CircleRoomTypeArg { Circle, Group, Photo }

fun CircleRoomTypeArg.toShareUrlType() = when (this) {
    CircleRoomTypeArg.Circle -> ShareUrlTypeArg.TIMELINE
    CircleRoomTypeArg.Group -> ShareUrlTypeArg.GROUP
    CircleRoomTypeArg.Photo -> ShareUrlTypeArg.GALLERY
}

fun convertToCircleRoomType(roomType: String?) = when (roomType) {
    GROUP_TYPE -> CircleRoomTypeArg.Group
    TIMELINE_TYPE -> CircleRoomTypeArg.Circle
    GALLERY_TYPE -> CircleRoomTypeArg.Photo
    else -> CircleRoomTypeArg.Group
}
