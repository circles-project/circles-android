package com.futo.circles.mapping

import com.futo.circles.model.CirclesUser
import org.matrix.android.sdk.api.session.user.model.User

fun User.toCirclesUser(isSelected: Boolean) = CirclesUser(
    id = userId,
    name = displayName ?: userId,
    avatarUrl = avatarUrl ?: "",
    isSelected = isSelected
)