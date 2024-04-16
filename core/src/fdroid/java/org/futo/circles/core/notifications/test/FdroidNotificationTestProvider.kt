package org.futo.circles.core.notifications.test

import org.futo.circles.core.feature.notifications.PushersManager
import org.futo.circles.core.feature.notifications.test.task.BaseNotificationTest
import org.futo.circles.core.feature.notifications.test.task.NotificationAccountSettingsTest
import org.futo.circles.core.feature.notifications.test.task.NotificationAvailableUnifiedDistributorsTest
import org.futo.circles.core.feature.notifications.test.task.NotificationCurrentPushDistributorTest
import org.futo.circles.core.feature.notifications.test.task.NotificationFromPushGatewayTest
import org.futo.circles.core.feature.notifications.test.task.NotificationPushRulesSettingsTest
import org.futo.circles.core.feature.notifications.test.task.NotificationSystemSettingsTest
import org.futo.circles.core.feature.notifications.test.task.NotificationTestSend
import org.futo.circles.core.feature.notifications.test.task.NotificationTestsProvider
import org.futo.circles.core.feature.notifications.test.task.NotificationsEndpointAsTokenRegistrationTest
import org.futo.circles.core.feature.notifications.test.task.NotificationsUnifiedPushEndpointTest
import org.futo.circles.core.feature.notifications.test.task.NotificationsUnifiedPushGatewayTest
import javax.inject.Inject

class FdroidNotificationTestProvider @Inject constructor(
    private val pushersManager: PushersManager,
    private val testSystemSettings: NotificationSystemSettingsTest,
    private val testAccountSettings: NotificationAccountSettingsTest,
    private val testPushRulesSettings: NotificationPushRulesSettingsTest,
    private val testCurrentUnifiedPushDistributor: NotificationCurrentPushDistributorTest,
    private val testAvailableUnifiedPushDistributors: NotificationAvailableUnifiedDistributorsTest,
    private val testBackgroundRestrictions: NotificationsBackgroundRestrictionsTest,
    private val testPushFromPushGateway: NotificationFromPushGatewayTest,
    private val testNotification: NotificationTestSend,
    private val testUnifiedPushGateway: NotificationsUnifiedPushGatewayTest,
    private val testUnifiedPushEndpoint: NotificationsUnifiedPushEndpointTest,
    private val testEndpointAsTokenRegistration: NotificationsEndpointAsTokenRegistrationTest
) : NotificationTestsProvider {

    override fun getTestsList(): List<BaseNotificationTest> {
        val list = mutableListOf<BaseNotificationTest>()
        list.add(testSystemSettings)
        list.add(testAccountSettings)
        list.add(testPushRulesSettings)
        list.add(testAvailableUnifiedPushDistributors)
        list.add(testCurrentUnifiedPushDistributor)
        if (pushersManager.isBackgroundSync()) {
            list.add(testBackgroundRestrictions)
        } else {
            list.add(testUnifiedPushGateway)
            list.add(testUnifiedPushEndpoint)
            list.add(testEndpointAsTokenRegistration)
            list.add(testPushFromPushGateway)
        }
        list.add(testNotification)
        return list
    }
}
