package org.futo.circles.di.ui

import org.futo.circles.feature.notifications.settings.PushNotificationsSettingsViewModel
import org.futo.circles.feature.notifications.test.NotificationTestViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val notificationsUiModule = module {
    viewModel { NotificationTestViewModel(get()) }
    viewModel { PushNotificationsSettingsViewModel(get()) }
}