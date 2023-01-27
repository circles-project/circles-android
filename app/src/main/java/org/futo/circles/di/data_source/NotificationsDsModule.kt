package org.futo.circles.di.data_source

import org.futo.circles.feature.notifications.NotificationUtils
import org.futo.circles.feature.notifications.PushersManager
import org.futo.circles.feature.notifications.UnifiedPushHelper
import org.futo.circles.feature.notifications.test.task.*
import org.koin.dsl.module

val notificationsDsModule = module {
    factory { PushersManager(get(), get()) }
    factory { UnifiedPushHelper(get(), get(), get()) }
    single { NotificationUtils(get()) }

    factory { NotificationAccountSettingsTest(get()) }
    factory { NotificationAvailableUnifiedDistributorsTest(get(), get(), get()) }
    factory { NotificationCurrentPushDistributorTest(get(), get()) }
    factory { NotificationPushRulesSettingsTest(get()) }
    factory { NotificationFromPushGatewayTest(get(), get()) }
    factory { NotificationsEndpointAsTokenRegistrationTest(get(), get()) }
    factory { NotificationSystemSettingsTest(get()) }
    factory { NotificationTestSend(get(), get()) }
    factory { NotificationUnifiedEndpointTest(get(), get()) }
    factory { NotificationUnifiedGatewayTest(get(), get()) }
}