package org.futo.circles.core.model

import org.futo.circles.core.list.IdEntity

sealed class WorkspaceTask(
    open val room: CirclesRoom,
    open val descriptionResId: Int,
    open val status: TaskStatus
) : IdEntity<Int>


data class MandatoryWorkspaceTask(
    override val room: CirclesRoom,
    override val descriptionResId: Int,
    override val status: TaskStatus = TaskStatus.IDLE
) : WorkspaceTask(room, descriptionResId, status) {
    override val id: Int = descriptionResId
}

data class OptionalWorkspaceTask(
    override val room: CirclesRoom,
    override val descriptionResId: Int,
    override val status: TaskStatus = TaskStatus.IDLE,
    val isSelected: Boolean = true
) : WorkspaceTask(room, descriptionResId, status) {
    override val id: Int = descriptionResId
}