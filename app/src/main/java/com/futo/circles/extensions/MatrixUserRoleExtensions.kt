package com.futo.circles.extensions

import com.futo.circles.R
import org.matrix.android.sdk.api.session.room.powerlevels.Role

fun Role.getRoleNameResId(): Int = when (this) {
    Role.Admin -> R.string.admin
    Role.Moderator -> R.string.moderator
    else -> R.string.user
}
