package org.futo.circles.feature.notifications

import android.content.Context
import org.futo.circles.R
import org.matrix.android.sdk.api.session.room.powerlevels.Role

class RoleFormatter(
    private val context: Context
) {
    fun format(role: Role): String {
        return when (role) {
            Role.Admin -> context.getString(R.string.power_level_admin)
            Role.Moderator -> context.getString(R.string.power_level_moderator)
            Role.Default -> context.getString(R.string.power_level_default)
            is Role.Custom -> context.getString(R.string.power_level_custom, role.value)
        }
    }
}
