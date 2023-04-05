package org.futo.circles.di

import org.futo.circles.di.api.apiModule
import org.futo.circles.di.data_source.*
import org.futo.circles.di.ui.*
import org.koin.core.module.Module

private val dataSourceModules = listOf(
    authDsModule,
    settingsDSModule,
    roomDSModule,
    photosDSModule,
    timelineDsModule,
    notificationsDsModule,
    flavourModule
)

private val uiModules = listOf(
    authUiModule,
    settingsUiModule,
    roomUiModule,
    photosUiModule,
    timelineUiModule,
    notificationsUiModule
)

val applicationModules = mutableListOf<Module>().apply {
    addAll(dataSourceModules)
    addAll(uiModules)
    add(apiModule)
}