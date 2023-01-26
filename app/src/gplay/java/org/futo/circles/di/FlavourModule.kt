package org.futo.circles.di

import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.notifications.GoogleFcmHelper
import org.futo.circles.notifications.test.NotificationTestTokenRegistration
import org.futo.circles.notifications.test.NotificationsTestFirebaseToken
import org.futo.circles.notifications.test.NotificationsTestPlayServices
import org.koin.dsl.module

val flavourModule = module {
    factory<FcmHelper> { GoogleFcmHelper(get(), get()) }

    factory { NotificationsTestFirebaseToken(get(), get()) }
    factory { NotificationsTestPlayServices(get()) }
    factory { NotificationTestTokenRegistration(get(), get()) }
}