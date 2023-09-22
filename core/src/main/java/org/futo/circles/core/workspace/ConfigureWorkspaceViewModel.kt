package org.futo.circles.core.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.core.R
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.Circle
import org.futo.circles.core.model.Gallery
import org.futo.circles.core.model.GroupsSpace
import org.futo.circles.core.model.MandatoryWorkspaceTask
import org.futo.circles.core.model.OptionalWorkspaceTask
import org.futo.circles.core.model.PeopleSpace
import org.futo.circles.core.model.PhotosSpace
import org.futo.circles.core.model.RootSpace
import org.futo.circles.core.model.SharedCirclesSpace
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.core.model.WorkspaceTask
import org.futo.circles.core.workspace.data_source.ConfigureWorkspaceDataSource
import javax.inject.Inject

@HiltViewModel
class ConfigureWorkspaceViewModel @Inject constructor(
    private val workspaceDataSource: ConfigureWorkspaceDataSource
) : ViewModel() {

    val tasksLiveData = MutableLiveData(
        listOf(
            MandatoryWorkspaceTask(RootSpace(), R.string.camera),
            MandatoryWorkspaceTask(GroupsSpace(), R.string.camera),
            MandatoryWorkspaceTask(PhotosSpace(), R.string.camera),
            MandatoryWorkspaceTask(PeopleSpace(), R.string.camera),
            MandatoryWorkspaceTask(SharedCirclesSpace(), R.string.camera),
            OptionalWorkspaceTask(Gallery(nameId = R.string.photos), R.string.camera),
            OptionalWorkspaceTask(Circle(nameId = R.string.friends), R.string.camera),
            OptionalWorkspaceTask(Circle(nameId = R.string.family), R.string.camera),
            OptionalWorkspaceTask(Circle(nameId = R.string.community), R.string.camera)
        )
    )
    val workspaceResultLiveData = SingleEventLiveData<Response<Unit>>()

    fun createWorkspace() = launchBg {
        val tasks = tasksLiveData.value?.toMutableList() ?: mutableListOf()
        tasks.forEachIndexed { i, item ->
            (item as? OptionalWorkspaceTask)?.let { if (!it.isSelected) return@forEachIndexed }
            if (item.status == TaskStatus.SUCCESS) return@forEachIndexed

            updateTaskStatus(tasks, i, TaskStatus.RUNNING)
            when (val result = createResult { workspaceDataSource.perform(item.room) }) {
                is Response.Error -> {
                    updateTaskStatus(tasks, i, TaskStatus.FAILED)
                    workspaceResultLiveData.postValue(result)
                    return@launchBg
                }

                is Response.Success -> updateTaskStatus(tasks, i, TaskStatus.SUCCESS)
            }
        }
        workspaceResultLiveData.postValue(Response.Success(Unit))
    }

    fun onOptionalTaskSelectionChanged(optionalWorkspaceTask: OptionalWorkspaceTask) {
        val newList = tasksLiveData.value?.toMutableList()?.apply {
            val index = indexOf(optionalWorkspaceTask).takeIf { it != -1 } ?: return
            add(index, optionalWorkspaceTask.copy(isSelected = !optionalWorkspaceTask.isSelected))
            remove(optionalWorkspaceTask)
        }
        tasksLiveData.postValue(newList)
    }

    private fun updateTaskStatus(tasks: List<WorkspaceTask>, index: Int, status: TaskStatus) {
        val task = tasks.getOrNull(index) ?: return
        val newList = tasks.toMutableList().apply {
            add(
                index, when (task) {
                    is MandatoryWorkspaceTask -> task.copy(status = status)
                    is OptionalWorkspaceTask -> task.copy(status = status)
                }
            )
            remove(task)
        }
        tasksLiveData.postValue(newList)
    }

}