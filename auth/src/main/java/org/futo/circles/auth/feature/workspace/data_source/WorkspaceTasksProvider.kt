package org.futo.circles.auth.feature.workspace.data_source

import org.futo.circles.auth.R
import org.futo.circles.auth.model.MandatoryWorkspaceTask
import org.futo.circles.auth.model.OptionalWorkspaceTask
import org.futo.circles.auth.model.WorkspaceTask
import org.futo.circles.core.model.Circle
import org.futo.circles.core.model.CirclesSpace
import org.futo.circles.core.model.Gallery
import org.futo.circles.core.model.GroupsSpace
import org.futo.circles.core.model.PeopleSpace
import org.futo.circles.core.model.PhotosSpace
import org.futo.circles.core.model.RootSpace
import org.futo.circles.core.model.SharedCirclesSpace
import javax.inject.Inject

class WorkspaceTasksProvider @Inject constructor() {

    fun getFullTasksList() = mutableListOf<WorkspaceTask>().apply {
        addAll(getMandatoryTasks())
        addAll(getOptionalTasks())
    }

    private fun getMandatoryTasks() = listOf(
        MandatoryWorkspaceTask(RootSpace(), R.string.root_space_description),
        MandatoryWorkspaceTask(CirclesSpace(), R.string.circles_space_description),
        MandatoryWorkspaceTask(GroupsSpace(), R.string.groups_space_description),
        MandatoryWorkspaceTask(PhotosSpace(), R.string.galleries_space_description),
        MandatoryWorkspaceTask(PeopleSpace(), R.string.people_space_description),
        MandatoryWorkspaceTask(SharedCirclesSpace(), R.string.profile_space_description)
    )

    private fun getOptionalTasks() = listOf(
        OptionalWorkspaceTask(
            Gallery(nameId = org.futo.circles.core.R.string.photos),
            R.string.photos_room_description
        ),
        OptionalWorkspaceTask(
            Circle(nameId = R.string.friends),
            R.string.friends_circle_description
        ),
        OptionalWorkspaceTask(Circle(nameId = R.string.family), R.string.family_circle_description),
        OptionalWorkspaceTask(
            Circle(nameId = R.string.community),
            R.string.community_circle_description
        )
    )

}