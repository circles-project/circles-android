package org.futo.circles.core.workspace.tasks

import org.futo.circles.core.model.CirclesRoom

abstract class BaseWorkspaceTask() {

    protected abstract val room: CirclesRoom
    protected abstract val titleResId: Int
    protected abstract val descriptionResId: Int
    protected var status: WorkspaceTaskStatus = WorkspaceTaskStatus.IDLE
    protected abstract fun validate()
    protected abstract fun perform()

    private var onTestUpdateListener: (() -> Unit)? = null


}