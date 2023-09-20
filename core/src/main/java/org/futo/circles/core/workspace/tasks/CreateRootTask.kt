package org.futo.circles.core.workspace.tasks

import org.futo.circles.core.model.CirclesRoom
import org.futo.circles.core.model.RootSpace

class CreateRootTask() : BaseWorkspaceTask() {

    override val room: CirclesRoom = RootSpace()
    override val titleResId: Int
        get() = TODO("Not yet implemented")
    override val descriptionResId: Int
        get() = TODO("Not yet implemented")

    override fun validate() {
        TODO("Not yet implemented")
    }

    override fun perform() {
        TODO("Not yet implemented")
    }
}