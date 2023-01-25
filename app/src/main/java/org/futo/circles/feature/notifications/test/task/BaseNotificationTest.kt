package org.futo.circles.feature.notifications.test.task

import androidx.annotation.StringRes
import org.futo.circles.model.NotificationTestStatus

abstract class BaseNotificationTest(@StringRes val titleResId: Int) {

    protected var description: String = ""
    protected var quickFix: NotificationQuickFix? = null
    protected var status: NotificationTestStatus = NotificationTestStatus.IDLE
    abstract fun perform()
}