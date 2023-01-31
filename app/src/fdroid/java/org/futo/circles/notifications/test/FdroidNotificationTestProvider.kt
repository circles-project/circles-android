package org.futo.circles.notifications.test

import org.futo.circles.feature.notifications.NotificationTestsProvider
import org.futo.circles.feature.notifications.test.task.*

class FdroidNotificationTestProvider(
    private val testSystemSettings: NotificationSystemSettingsTest,
    private val testAccountSettings: NotificationAccountSettingsTest,
    private val testPushRulesSettings: NotificationPushRulesSettingsTest,
    private val testCurrentUnifiedPushDistributor: NotificationCurrentPushDistributorTest,
    private val testAvailableUnifiedPushDistributors: NotificationAvailableUnifiedDistributorsTest,
    private val testBackgroundRestrictions: NotificationsBackgroundRestrictionsTest,
    private val testBatteryOptimization: NotificationsBatteryOptimizationTest,
    private val testNotification: NotificationTestSend
) : NotificationTestsProvider {

    override fun getTestsList(): List<BaseNotificationTest> {
        val list = mutableListOf<BaseNotificationTest>()
        list.add(testSystemSettings)
        list.add(testAccountSettings)
        list.add(testPushRulesSettings)
        list.add(testAvailableUnifiedPushDistributors)
        list.add(testCurrentUnifiedPushDistributor)
        list.add(testBackgroundRestrictions)
        list.add(testBatteryOptimization)
        list.add(testNotification)
        return list
    }
}
