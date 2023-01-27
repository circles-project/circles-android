package org.futo.circles.model

import org.futo.circles.core.list.IdEntity

data class NotificationTestListItem(
    val titleId: Int,
    val message: String,
    val status: NotificationTestStatus,
    val hasFix: Boolean
) : IdEntity<Int> {
    override val id: Int = titleId
}