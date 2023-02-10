package org.futo.circles.feature.notifications.test.task

import org.futo.circles.feature.notifications.test.task.BaseNotificationTest

interface NotificationTestsProvider {
    fun getTestsList(): List<BaseNotificationTest>
}