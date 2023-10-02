package org.futo.circles.core.model

import org.futo.circles.core.list.IdEntity
import org.matrix.android.sdk.api.session.room.powerlevels.Role

data class AccessLevelListItem(
    val role: Role,
    val isSelected: Boolean = false
) : IdEntity<String> {
    override val id: String = role.value.toString()
}