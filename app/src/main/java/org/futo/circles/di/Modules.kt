package org.futo.circles.di

import org.futo.circles.di.data_source.authDsModule
import org.futo.circles.di.data_source.roomDSModule
import org.futo.circles.di.data_source.settingsDSModule
import org.futo.circles.di.data_source.timelineDsModule
import org.futo.circles.di.ui.authUiModule
import org.futo.circles.di.ui.roomUiModule
import org.futo.circles.di.ui.settingsUiModule
import org.futo.circles.di.ui.timelineUiModule
import org.koin.core.module.Module

private val dataSourceModules = listOf(
    authDsModule,
    settingsDSModule,
    roomDSModule,
    timelineDsModule
)

private val uiModules = listOf(
    authUiModule,
    settingsUiModule,
    roomUiModule,
    timelineUiModule
)

val applicationModules = mutableListOf<Module>().apply {
    addAll(dataSourceModules)
    addAll(uiModules)
}