package org.futo.circles.core.extensions

import org.futo.circles.core.utils.UserIdUtils
import org.matrix.android.sdk.api.session.room.model.RoomMemberSummary
import org.matrix.android.sdk.api.session.room.sender.SenderInfo
import org.matrix.android.sdk.api.session.user.model.User


fun User.notEmptyDisplayName(): String = getName(userId, displayName)

fun RoomMemberSummary.notEmptyDisplayName(): String = getName(userId, displayName)

fun SenderInfo.notEmptyDisplayName(): String = getName(userId, displayName)

private fun getName(userId: String, displayName: String?): String {
    val name = displayName?.takeIf { it.isNotEmpty() }
        ?: userId.replace("@", "").substringBefore(":")
    return UserIdUtils.removeDomainSuffix(name)
}