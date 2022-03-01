package com.futo.circles.di

import com.futo.circles.feature.groups.GroupsViewModel
import com.futo.circles.feature.groups.timeline.GroupTimelineViewModel
import com.futo.circles.feature.groups.timeline.invite.InviteMembersViewModel
import com.futo.circles.feature.log_in.LogInViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { LogInViewModel(get()) }
    viewModel { GroupsViewModel() }
    viewModel { (roomId: String) -> GroupTimelineViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> InviteMembersViewModel(get { parametersOf(roomId) }) }
}