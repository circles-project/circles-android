package com.futo.circles.di

import com.futo.circles.feature.create_group.CreateGroupViewModel
import com.futo.circles.feature.group_invite.InviteMembersViewModel
import com.futo.circles.feature.group_timeline.GroupTimelineViewModel
import com.futo.circles.feature.groups.GroupsViewModel
import com.futo.circles.feature.log_in.LogInViewModel
import com.futo.circles.feature.manage_group_members.ManageGroupMembersViewModel
import com.futo.circles.feature.select_users.SelectUsersViewModel
import com.futo.circles.feature.sign_up.SignUpViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { LogInViewModel(get()) }
    viewModel { GroupsViewModel() }
    viewModel { (roomId: String) -> GroupTimelineViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> InviteMembersViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> ManageGroupMembersViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String?) -> SelectUsersViewModel(get { parametersOf(roomId) }) }
    viewModel { CreateGroupViewModel(get()) }
    viewModel { SignUpViewModel(get()) }
}