package org.futo.circles.auth.feature.workspace.list

import android.view.ViewGroup
import org.futo.circles.core.base.list.BaseRvAdapter
import org.futo.circles.auth.model.MandatoryWorkspaceTask
import org.futo.circles.auth.model.OptionalWorkspaceTask
import org.futo.circles.auth.model.WorkspaceTask

private enum class WorkspaceTaskViewType { Mandatory, Optional }

class WorkspaceTasksListAdapter(
    private val onOptionalItemClicked: (OptionalWorkspaceTask) -> Unit
) : BaseRvAdapter<WorkspaceTask, WorkspaceTaskViewHolder>(DefaultIdEntityCallback()) {

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is MandatoryWorkspaceTask -> WorkspaceTaskViewType.Mandatory.ordinal
        is OptionalWorkspaceTask -> WorkspaceTaskViewType.Optional.ordinal
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ) = when (WorkspaceTaskViewType.values()[viewType]) {
        WorkspaceTaskViewType.Mandatory -> MandatoryWorkspaceTaskViewHolder(parent = parent)

        WorkspaceTaskViewType.Optional -> OptionalWorkspaceTaskViewHolder(
            parent = parent,
            onItemClicked = { position ->
                (getItem(position) as? OptionalWorkspaceTask)?.let {
                    onOptionalItemClicked.invoke(it)
                }
            }
        )
    }

    override fun onBindViewHolder(holder: WorkspaceTaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}