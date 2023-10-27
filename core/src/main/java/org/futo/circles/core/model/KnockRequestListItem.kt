package org.futo.circles.core.model

import org.futo.circles.core.base.list.IdEntity

data class KnockRequestListItem(
    val requesterId: String,
    val requesterName: String,
    val requesterAvatarUrl: String?,
    val message: String?
) : IdEntity<String> {
    override val id: String = requesterId
}

fun KnockRequestListItem.toCircleUser() = CirclesUserSummary(
    requesterId, requesterName, requesterAvatarUrl ?: ""
)