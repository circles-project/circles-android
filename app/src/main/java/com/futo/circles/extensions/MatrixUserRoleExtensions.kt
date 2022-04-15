package com.futo.circles.extensions

import com.futo.circles.R
import com.futo.circles.provider.MatrixSessionProvider
import org.matrix.android.sdk.api.session.events.model.EventType
import org.matrix.android.sdk.api.session.room.model.PowerLevelsContent
import org.matrix.android.sdk.api.session.room.powerlevels.PowerLevelsHelper
import org.matrix.android.sdk.api.session.room.powerlevels.Role

fun Role.getRoleNameResId(): Int = when (this) {
    Role.Admin -> R.string.admin
    Role.Moderator -> R.string.moderator
    else -> R.string.user
}

fun PowerLevelsContent.isCurrentUserAbleToPost(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAllowedToSend(userId, false, EventType.MESSAGE)
}

fun PowerLevelsContent.isCurrentUserAbleToInvite(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToInvite(userId)
}

fun PowerLevelsContent.isCurrentUserAbleToChangeSettings(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToRedact(userId)
}

fun PowerLevelsContent.isCurrentUserAbleToBan(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToBan(userId)
}

fun PowerLevelsContent.isCurrentUserAbleToKick(): Boolean {
    val userId = MatrixSessionProvider.currentSession?.myUserId ?: return false
    return PowerLevelsHelper(this).isUserAbleToKick(userId)
}