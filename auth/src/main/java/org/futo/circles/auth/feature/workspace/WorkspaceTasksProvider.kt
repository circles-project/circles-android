package org.futo.circles.auth.feature.workspace

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import org.futo.circles.auth.R
import org.futo.circles.auth.model.SetupCirclesListItem
import org.futo.circles.core.model.ChatsSpace
import org.futo.circles.core.model.GroupsSpace
import org.futo.circles.core.model.PhotosSpace
import org.futo.circles.core.model.RootSpace
import org.futo.circles.core.model.TimelinesSpace
import javax.inject.Inject

class WorkspaceTasksProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    fun getMandatoryTasks() = listOf(
        RootSpace(),
        TimelinesSpace(),
        GroupsSpace(),
        PhotosSpace(),
        ChatsSpace()
    )

    fun getInitialSetupCirclesList(): List<SetupCirclesListItem> = listOf(
        SetupCirclesListItem(context.getString(R.string.family)),
        SetupCirclesListItem(context.getString(R.string.friends))
    )

}