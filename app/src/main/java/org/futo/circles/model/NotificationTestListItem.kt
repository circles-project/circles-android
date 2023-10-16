package org.futo.circles.model

import org.futo.circles.core.base.list.IdEntity
import org.futo.circles.core.model.TaskStatus

data class NotificationTestListItem(
    val titleId: Int,
    val message: String,
    val status: TaskStatus,
    val hasFix: Boolean
) : IdEntity<Int> {
    override val id: Int = titleId
}