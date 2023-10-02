package org.futo.circles.feature.notifications.test.task

import androidx.annotation.StringRes
import org.futo.circles.core.model.TaskStatus
import org.futo.circles.model.NotificationTestListItem

abstract class BaseNotificationTest(@StringRes val titleResId: Int) {

    protected var description: String = ""
    protected var quickFix: NotificationQuickFix? = null
    protected var status: TaskStatus = TaskStatus.IDLE
    protected abstract fun perform()

    private var onTestUpdateListener: ((NotificationTestListItem) -> Unit)? = null
    fun runTest(updateListener: (NotificationTestListItem) -> Unit) {
        onTestUpdateListener = updateListener
        status = TaskStatus.RUNNING
        description = ""
        quickFix = null
        updateTestInfo()
        perform()
        updateTestInfo()
    }

    fun runFix() {
        status = TaskStatus.RUNNING
        description = ""
        quickFix = null
        updateTestInfo()
        quickFix?.runFix()
        updateTestInfo()
    }

    fun toListItem() = NotificationTestListItem(
        titleResId, description, status, quickFix != null
    )

    protected fun updateTestInfo() {
        onTestUpdateListener?.invoke(toListItem())
    }

}