package org.futo.circles.auth.feature.workspace

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.futo.circles.auth.feature.workspace.data_source.ConfigureWorkspaceDataSource
import org.futo.circles.auth.feature.workspace.data_source.WorkspaceTasksProvider
import org.futo.circles.auth.model.MandatoryWorkspaceTask
import org.futo.circles.auth.model.OptionalWorkspaceTask
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.TaskStatus
import javax.inject.Inject

@HiltViewModel
class ConfigureWorkspaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    workspaceTasksProvider: WorkspaceTasksProvider,
    private val workspaceDataSource: ConfigureWorkspaceDataSource
) : ViewModel() {

    private val shouldValidate =
        savedStateHandle.get<Boolean>(ConfigureWorkspaceFragment.SHOULD_VALIDATE) ?: false
    val tasksLiveData = MutableLiveData(
        if (shouldValidate) workspaceTasksProvider.getMandatoryTasks()
        else workspaceTasksProvider.getFullTasksList()
    )
    val workspaceResultLiveData = SingleEventLiveData<Response<Unit>>()
    val validateWorkspaceResultLiveData = SingleEventLiveData<Response<Unit>>()

    init {
        if (shouldValidate) validateWorkspace()
    }

    private fun validateWorkspace() = launchBg {
        val tasks = tasksLiveData.value?.toMutableList() ?: mutableListOf()
        var hasError = false
        tasks.forEachIndexed { i, item ->
            updateTaskStatus(i, TaskStatus.RUNNING)
            when (createResult { workspaceDataSource.validate(item.room) }) {
                is Response.Error -> {
                    hasError = true
                    updateTaskStatus(i, TaskStatus.FAILED)
                }

                is Response.Success -> updateTaskStatus(i, TaskStatus.SUCCESS)
            }
        }
        validateWorkspaceResultLiveData.postValue(
            if (hasError) Response.Error("") else Response.Success(Unit)
        )
    }

    fun createWorkspace() = launchBg {
        val tasks = tasksLiveData.value?.toMutableList() ?: mutableListOf()
        tasks.forEachIndexed { i, item ->
            (item as? OptionalWorkspaceTask)?.let { if (!it.isSelected) return@forEachIndexed }
            if (item.status == TaskStatus.SUCCESS) return@forEachIndexed

            updateTaskStatus(i, TaskStatus.RUNNING)
            when (val result = createResult { workspaceDataSource.performCreateOrFix(item.room) }) {
                is Response.Error -> {
                    updateTaskStatus(i, TaskStatus.FAILED)
                    workspaceResultLiveData.postValue(result)
                    return@launchBg
                }

                is Response.Success -> updateTaskStatus(i, TaskStatus.SUCCESS)
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
        tasksLiveData.value = newList
    }

    private suspend fun updateTaskStatus(index: Int, status: TaskStatus) {
        val tasks = tasksLiveData.value?.toMutableList() ?: mutableListOf()
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
        withContext(Dispatchers.Main) { tasksLiveData.value = newList }
    }

}