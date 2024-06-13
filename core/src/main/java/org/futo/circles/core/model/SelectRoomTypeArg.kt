package org.futo.circles.core.model

enum class SelectRoomTypeArg {
    CirclesJoined,
    GroupsJoined,
    PhotosJoined,
    MyCircleNotJoinedByUser
}

fun SelectRoomTypeArg.isCircle() =
    this == SelectRoomTypeArg.CirclesJoined || this == SelectRoomTypeArg.MyCircleNotJoinedByUser