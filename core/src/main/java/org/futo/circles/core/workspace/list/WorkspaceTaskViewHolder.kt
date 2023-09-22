package org.futo.circles.core.workspace.list

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import org.futo.circles.core.R
import org.futo.circles.core.databinding.ListItemMandatoryWorkspaceTaskBinding
import org.futo.circles.core.databinding.ListItemOptionalWorkspaceTaskBinding
import org.futo.circles.core.extensions.gone
import org.futo.circles.core.extensions.onClick
import org.futo.circles.core.extensions.visible
import org.futo.circles.core.list.ViewBindingHolder
import org.futo.circles.core.list.context
import org.futo.circles.core.model.OptionalWorkspaceTask
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.core.model.WorkspaceTask


abstract class WorkspaceTaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    abstract fun bind(data: WorkspaceTask)

    protected fun bindStatus(status: TaskStatus, ivStatus: ImageView, vTaskProgress: ProgressBar) {
        when (status) {
            TaskStatus.RUNNING -> {
                ivStatus.gone()
                vTaskProgress.visible()
            }

            TaskStatus.IDLE -> {
                ivStatus.gone()
                vTaskProgress.gone()
            }

            else -> {
                vTaskProgress.gone()
                ivStatus.apply {
                    visible()
                    setImageResource(
                        if (status == TaskStatus.FAILED) R.drawable.ic_error
                        else R.drawable.ic_check_circle
                    )
                    setColorFilter(
                        ContextCompat.getColor(
                            context,
                            if (status == TaskStatus.FAILED) R.color.red
                            else R.color.blue
                        )
                    )
                }
            }
        }
    }

}

class MandatoryWorkspaceTaskViewHolder(
    parent: ViewGroup,
) : WorkspaceTaskViewHolder(inflate(parent, ListItemMandatoryWorkspaceTaskBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemMandatoryWorkspaceTaskBinding

    override fun bind(data: WorkspaceTask) {
        with(binding) {
            tvName.text = context.getString(data.titleResId)
            tvMessage.text = context.getString(data.descriptionResId)
            bindStatus(data.status, ivTaskStatus, taskProgress)
        }
    }
}

class OptionalWorkspaceTaskViewHolder(
    parent: ViewGroup,
    onItemClicked: (Int) -> Unit
) : WorkspaceTaskViewHolder(inflate(parent, ListItemOptionalWorkspaceTaskBinding::inflate)) {

    private companion object : ViewBindingHolder

    private val binding = baseBinding as ListItemOptionalWorkspaceTaskBinding

    init {
        onClick(binding.rootItem) { onItemClicked(it) }
    }

    override fun bind(data: WorkspaceTask) {
        with(binding.lTask) {
            tvName.text = context.getString(data.titleResId)
            tvMessage.text = context.getString(data.descriptionResId)
            bindStatus(data.status, ivTaskStatus, taskProgress)
        }
        binding.taskCheck.isChecked = (data as? OptionalWorkspaceTask)?.isSelected ?: false
    }
}