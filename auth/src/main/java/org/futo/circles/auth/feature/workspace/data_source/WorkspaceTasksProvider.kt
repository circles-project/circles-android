package org.futo.circles.auth.feature.workspace.data_source

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.model.SetupCirclesListItem
import org.futo.circles.core.model.CirclesSpace
import org.futo.circles.core.model.GroupsSpace
import org.futo.circles.core.model.PeopleSpace
import org.futo.circles.core.model.PhotosSpace
import org.futo.circles.core.model.RootSpace
import org.futo.circles.core.model.SharedCirclesSpace
import javax.inject.Inject

class WorkspaceTasksProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getMandatoryTasks() = listOf(
        RootSpace(),
        CirclesSpace(),
        GroupsSpace(),
        PhotosSpace(),
        PeopleSpace(),
        SharedCirclesSpace()
    )

    fun getInitialSetupCirclesList(): List<SetupCirclesListItem> = listOf(
        SetupCirclesListItem(context.getString(R.string.family)),
        SetupCirclesListItem(context.getString(R.string.friends))
    )

}