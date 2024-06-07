package org.futo.circles.auth.feature.setup.circles

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import org.futo.circles.auth.R
import org.futo.circles.auth.feature.workspace.data_source.ConfigureWorkspaceDataSource
import org.futo.circles.auth.feature.workspace.data_source.WorkspaceTasksProvider
import org.futo.circles.auth.model.SetupCirclesListItem
import org.futo.circles.auth.model.WorkspaceTask
import org.futo.circles.core.base.SingleEventLiveData
import org.futo.circles.core.extensions.Response
import org.futo.circles.core.extensions.createResult
import org.futo.circles.core.extensions.launchBg
import org.futo.circles.core.model.Circle
import org.futo.circles.core.model.LoadingData
import javax.inject.Inject

@HiltViewModel
class SetupCirclesViewModel @Inject constructor(
    private val workspaceTasksProvider: WorkspaceTasksProvider,
    private val workspaceDataSource: ConfigureWorkspaceDataSource
) : ViewModel() {

    val circlesLiveData = MutableLiveData(workspaceTasksProvider.getInitialSetupCirclesList())
    val workspaceResultLiveData = SingleEventLiveData<Response<Unit>>()
    val workspaceLoadingLiveData = SingleEventLiveData<LoadingData>()

    fun setImageUriForCircle(id: String, uri: Uri) {
        val list = circlesLiveData.value?.toMutableList() ?: mutableListOf()
        val newList = list.map { item -> if (item.id == id) item.copy(uri = uri) else item }
        circlesLiveData.value = newList
    }

    fun removeCircle(id: String) {
        val list = circlesLiveData.value?.toMutableList() ?: mutableListOf()
        list.removeIf { it.id == id }
        circlesLiveData.value = list
    }

    fun addCircleItem(name: String) {
        val list = circlesLiveData.value?.toMutableList() ?: mutableListOf()
        if (list.firstOrNull { it.name == name } == null) {
            list.add(SetupCirclesListItem(name))
            circlesLiveData.value = list
        }
    }

    fun createWorkspace() = launchBg {
        val tasks = getAllWorkspaceTask()
        tasks.forEachIndexed { i, item ->
            workspaceLoadingLiveData.postValue(
                LoadingData(
                    messageId = R.string.configuring_workspace,
                    isLoading = true,
                    progress = i,
                    total = tasks.size
                )
            )
            val result = createResult { workspaceDataSource.performCreateOrFix(item) }
            (result as? Response.Error)?.let {
                workspaceLoadingLiveData.postValue(LoadingData(isLoading = false))
                workspaceResultLiveData.postValue(result)
                return@launchBg
            }
            workspaceLoadingLiveData.postValue(LoadingData(isLoading = false))
            workspaceResultLiveData.postValue(Response.Success(Unit))
        }
    }

    private fun getAllWorkspaceTask(): List<WorkspaceTask> {
        val tasks = workspaceTasksProvider.getMandatoryTasks().map { WorkspaceTask(it) }
        val circlesTasks = (circlesLiveData.value ?: emptyList()).map {
            WorkspaceTask(Circle(), it.name, it.uri)
        }
        return tasks + circlesTasks
    }

}