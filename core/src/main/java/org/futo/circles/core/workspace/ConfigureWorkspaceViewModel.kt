package org.futo.circles.core.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.R
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.Gallery
import org.futo.circles.core.model.GroupsSpace
import org.futo.circles.core.model.MandatoryWorkspaceTask
import org.futo.circles.core.model.OptionalWorkspaceTask
import org.futo.circles.core.model.PeopleSpace
import org.futo.circles.core.model.PhotosSpace
import org.futo.circles.core.model.RootSpace
import org.futo.circles.core.model.SharedCirclesSpace
import javax.inject.Inject

@HiltViewModel
class ConfigureWorkspaceViewModel @Inject constructor(
    private val workspaceDataSource: ConfigureWorkspaceDataSource
) : ViewModel() {

    val tasksLiveData = MutableLiveData(
        listOf(
            MandatoryWorkspaceTask(RootSpace(), R.string.camera, R.string.camera),
            MandatoryWorkspaceTask(GroupsSpace(), R.string.camera, R.string.camera),
            MandatoryWorkspaceTask(PhotosSpace(), R.string.camera, R.string.camera),
            MandatoryWorkspaceTask(PeopleSpace(), R.string.camera, R.string.camera),
            MandatoryWorkspaceTask(SharedCirclesSpace(), R.string.camera, R.string.camera),
            OptionalWorkspaceTask(
                Gallery(nameId = R.string.photos),
                R.string.camera,
                R.string.camera
            ),
            OptionalWorkspaceTask(
                Gallery(nameId = R.string.photos),
                R.string.camera,
                R.string.camera
            ),
            OptionalWorkspaceTask(
                Gallery(nameId = R.string.photos),
                R.string.camera,
                R.string.camera
            ),
            OptionalWorkspaceTask(
                Gallery(nameId = R.string.photos),
                R.string.camera,
                R.string.camera
            )
        )
    )

    suspend fun createWorkspace() {
        launchBg {

        }
    }

}