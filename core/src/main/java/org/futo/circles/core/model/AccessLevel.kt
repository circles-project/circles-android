package org.futo.circles.core.model

import org.matrix.android.sdk.api.session.room.powerlevels.Role

enum class AccessLevel(val levelValue: Int) {
    Admin(Role.Admin.value), Moderator(Role.Moderator.value), User(Role.Default.value)
}