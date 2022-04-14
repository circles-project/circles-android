package com.futo.circles.extensions

import com.futo.circles.R
import org.matrix.android.sdk.api.session.room.powerlevels.Role

fun Role.getRoleNameResId(): Int = when (value) {
    10 -> R.string.can_post
    50 -> R.string.moderator
    100 -> R.string.owner
    else -> R.string.can_view
}