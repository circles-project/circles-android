package org.futo.circles.di.data_source

import org.futo.circles.feature.notifications.*
import org.futo.circles.feature.notifications.test.task.*
import org.koin.dsl.module

val notificationsDsModule = module {
    factory { PushersManager(get(), get()) }
    single { NotificationUtils(get()) }
    factory { NotificationAccountSettingsTest(get()) }
    factory { NotificationAvailableUnifiedDistributorsTest(get(), get(), get()) }
    factory { NotificationCurrentPushDistributorTest(get(), get()) }
    factory { NotificationPushRulesSettingsTest(get()) }
    factory { NotificationFromPushGatewayTest(get(), get()) }
    factory { NotificationSystemSettingsTest(get()) }
    factory { NotificationTestSend(get(), get()) }
    factory { PushHandler(get(), get(), get()) }
    single { NotificationDrawerManager(get(), get(), get(), get()) }
    factory { NotifiableEventResolver(get(), get()) }
    factory { NotifiableEventProcessor() }
    factory { NotificationRenderer(get(), get(), get()) }
    factory { NotificationEventPersistence(get()) }
    factory { RoomGroupMessageCreator(get(), get(), get()) }
    factory { NotificationBitmapLoader(get()) }
    factory { DisplayableEventFormatter(get()) }
    single { PushRuleTriggerListener(get(), get()) }
    factory { ShortcutCreator(get()) }
    factory { ShortcutsHandler(get(), get()) }
    factory { UnifiedPushHelper(get(), get(), get()) }
}