package com.futo.circles.di

import com.futo.circles.feature.circles.CirclesViewModel
import com.futo.circles.feature.configure_group.ConfigureGroupViewModel
import com.futo.circles.feature.create_group.CreateGroupViewModel
import com.futo.circles.feature.group_invite.InviteMembersViewModel
import com.futo.circles.feature.group_members.ManageGroupMembersViewModel
import com.futo.circles.feature.group_members.change_role.ChangeAccessLevelViewModel
import com.futo.circles.feature.group_timeline.GroupTimelineViewModel
import com.futo.circles.feature.groups.GroupsViewModel
import com.futo.circles.feature.home.HomeViewModel
import com.futo.circles.feature.log_in.LogInViewModel
import com.futo.circles.feature.post.CreatePostViewModel
import com.futo.circles.feature.report.ReportViewModel
import com.futo.circles.feature.select_users.SelectUsersViewModel
import com.futo.circles.feature.setup_circles.SetupCirclesViewModel
import com.futo.circles.feature.setup_profile.SetupProfileViewModel
import com.futo.circles.feature.sign_up.SignUpViewModel
import com.futo.circles.feature.sign_up_type.SelectSignUpTypeViewModel
import com.futo.circles.feature.terms.AcceptTermsViewModel
import com.futo.circles.feature.validate_email.ValidateEmailViewModel
import com.futo.circles.feature.validate_token.ValidateTokenViewModel
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
    viewModel { ValidateTokenViewModel(get()) }
    viewModel { SelectSignUpTypeViewModel(get()) }
    viewModel { AcceptTermsViewModel(get()) }
    viewModel { ValidateEmailViewModel(get()) }
    viewModel { SetupProfileViewModel(get()) }
    viewModel { SetupCirclesViewModel(get(), get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { CirclesViewModel() }
    viewModel { (levelValue: Int, myUserLevelValue: Int) ->
        ChangeAccessLevelViewModel(get { parametersOf(levelValue, myUserLevelValue) })
    }
    viewModel { (roomId: String) -> ConfigureGroupViewModel(get { parametersOf(roomId) }) }
    viewModel { CreatePostViewModel() }
    viewModel { (roomId: String, eventId: String) ->
        ReportViewModel(get { parametersOf(roomId, eventId) })
    }
}