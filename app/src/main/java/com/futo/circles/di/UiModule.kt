package com.futo.circles.di

import com.futo.circles.ui.groups.GroupsViewModel
import com.futo.circles.ui.groups.timeline.GroupTimelineViewModel
import com.futo.circles.ui.log_in.LogInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { LogInViewModel(get()) }
    viewModel { GroupsViewModel(get()) }
    viewModel { (roomId: String) -> GroupTimelineViewModel(get { parametersOf(roomId) }) }
}