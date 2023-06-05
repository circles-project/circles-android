package org.futo.circles.core.provider

import android.content.Context
import org.futo.circles.core.R
import org.matrix.android.sdk.api.provider.RoomDisplayNameFallbackProvider


class RoomDisplayNameFallbackProviderImpl(private val context: Context) :
    RoomDisplayNameFallbackProvider {

    override fun getNameForRoomInvite() = context.getString(R.string.name_for_room_invite)

    override fun getNameForEmptyRoom(isDirect: Boolean, leftMemberNames: List<String>) =
        context.getString(R.string.name_for_empty_room)

    override fun getNameFor1member(name: String) = name

    override fun getNameFor2members(name1: String, name2: String) =
        "$name1 ${context.getString(R.string.and)} $name2"

    override fun getNameFor3members(name1: String, name2: String, name3: String) =
        "$name1, $name2 ${context.getString(R.string.and)} $name3"

    override fun getNameFor4members(name1: String, name2: String, name3: String, name4: String) =
        "$name1, $name2, $name3 ${context.getString(R.string.and)} $name4"

    override fun getNameFor4membersAndMore(
        name1: String,
        name2: String,
        name3: String,
        remainingCount: Int
    ) = "$name1, $name2, $name3 ${context.getString(R.string.and)} $remainingCount ${
        context.getString(R.string.other)
    }"
}
