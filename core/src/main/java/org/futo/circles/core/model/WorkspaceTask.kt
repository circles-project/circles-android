package org.futo.circles.core.model

import org.futo.circles.core.list.IdEntity

sealed class WorkspaceTask(
    open val room: CirclesRoom,
    open val titleResId: Int,
    open val descriptionResId: Int,
    open val status: TaskStatus
) : IdEntity<Int>

data class MandatoryWorkspaceTask(
    override val room: CirclesRoom,
    override val titleResId: Int,
    override val descriptionResId: Int,
    override val status: TaskStatus = TaskStatus.IDLE
) : WorkspaceTask(room, titleResId, descriptionResId, status) {
    override val id: Int = titleResId
}

data class OptionalWorkspaceTask(
    override val room: CirclesRoom,
    override val titleResId: Int,
    override val descriptionResId: Int,
    override val status: TaskStatus = TaskStatus.IDLE,
    val isSelected: Boolean = true
) : WorkspaceTask(room, titleResId, descriptionResId, status) {
    override val id: Int = titleResId
}