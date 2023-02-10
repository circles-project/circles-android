package org.futo.circles.notifications.test

import org.futo.circles.feature.notifications.test.task.NotificationTestsProvider
import org.futo.circles.feature.notifications.test.task.*

class GoogleNotificationTestProvider(
    private val testSystemSettings: NotificationSystemSettingsTest,
    private val testAccountSettings: NotificationAccountSettingsTest,
    private val testPushRulesSettings: NotificationPushRulesSettingsTest,
    private val testCurrentUnifiedPushDistributor: NotificationCurrentPushDistributorTest,
    private val testAvailableUnifiedPushDistributors: NotificationAvailableUnifiedDistributorsTest,
    private val testPushFromPushGateway: NotificationFromPushGatewayTest,
    private val testPlayServices: NotificationsTestPlayServices,
    private val testFirebaseToken: NotificationsTestFirebaseToken,
    private val testTokenRegistration: NotificationTestTokenRegistration,
    private val testNotification: NotificationTestSend
) : NotificationTestsProvider {

    override fun getTestsList(): List<BaseNotificationTest> {
        val list = mutableListOf<BaseNotificationTest>()
        list.add(testSystemSettings)
        list.add(testAccountSettings)
        list.add(testPushRulesSettings)
        list.add(testAvailableUnifiedPushDistributors)
        list.add(testCurrentUnifiedPushDistributor)
        list.add(testPlayServices)
        list.add(testFirebaseToken)
        list.add(testTokenRegistration)
        list.add(testPushFromPushGateway)
        list.add(testNotification)
        return list
    }
}
