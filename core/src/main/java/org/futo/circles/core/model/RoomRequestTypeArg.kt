package org.futo.circles.core.model

enum class RoomRequestTypeArg { Circle, Group, Photo, DM }


fun RoomRequestTypeArg.toRoomTypeString() = when (this) {
    RoomRequestTypeArg.Circle -> TIMELINE_TYPE
    RoomRequestTypeArg.Group -> GROUP_TYPE
    RoomRequestTypeArg.Photo -> GALLERY_TYPE
    RoomRequestTypeArg.DM -> ""
}