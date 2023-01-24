package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class NotificationTestListItem(
    val name: String,
    val message: String,
    val status: NotificationTestStatus
) : IdEntity<String> {
    override val id: String = name
}