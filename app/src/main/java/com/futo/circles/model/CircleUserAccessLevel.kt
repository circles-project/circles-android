package com.futo.circles.model

import androidx.annotation.StringRes
import com.futo.circles.R

enum class CircleUserAccessLevel(powerLevel: Int, @StringRes nameResId: Int) {
    CanView(0, R.string.can_view),
    CanPost(10, R.string.can_post),
    Moderator(50, R.string.moderator),
    Owner(100, R.string.owner)
}