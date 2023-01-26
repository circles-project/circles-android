package org.futo.circles.subscriptions.di

import org.futo.circles.feature.notifications.FcmHelper
import org.futo.circles.subscriptions.notifications.GoogleFcmHelper
import org.koin.dsl.module

val flavourModule = module {
    factory<FcmHelper> { GoogleFcmHelper(get(), get()) }
}