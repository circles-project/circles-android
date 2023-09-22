package org.futo.circles.auth.feature.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.feature.workspace.data_source.ConfigureWorkspaceDataSource
import org.futo.circles.auth.feature.workspace.data_source.WorkspaceTasksProvider
import org.futo.circles.auth.model.MandatoryWorkspaceTask
import org.futo.circles.auth.model.OptionalWorkspaceTask
import org.futo.circles.auth.model.WorkspaceTask
import org.futo.circles.core.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.TaskStatus
import javax.inject.Inject

@HiltViewModel
class ConfigureWorkspaceViewModel @Inject constructor(
    private val workspaceDataSource: ConfigureWorkspaceDataSource,
    private val workspaceTasksProvider: WorkspaceTasksProvider
) : ViewModel() {

    val tasksLiveData = MutableLiveData(workspaceTasksProvider.getFullTasksList())
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