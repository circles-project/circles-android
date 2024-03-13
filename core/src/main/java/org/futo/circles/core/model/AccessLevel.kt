package org.futo.circles.core.model

import org.futo.circles.core.base.READ_ONLY_ROLE
import org.matrix.android.sdk.api.session.room.powerlevels.Role

enum class AccessLevel(val levelValue: Int) {
    Admin(Role.Admin.value),
    Moderator(Role.Moderator.value),
    User(Role.Default.value),
    ReadOnly(READ_ONLY_ROLE);

    companion object {
        fun fromValue(value: Int): AccessLevel =
            AccessLevel.entries.firstOrNull { it.levelValue == value } ?: User
    }
}

