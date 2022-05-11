package com.futo.circles.di

import com.futo.circles.feature.circles.CirclesViewModel
import com.futo.circles.feature.circles.following.FollowingViewModel
import com.futo.circles.feature.groups.GroupsViewModel
import com.futo.circles.feature.home.HomeViewModel
import com.futo.circles.feature.log_in.LogInViewModel
import com.futo.circles.feature.room.create_room.CreateRoomViewModel
import com.futo.circles.feature.room.invite.InviteMembersViewModel
import com.futo.circles.feature.room.manage_members.ManageMembersViewModel
import com.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelViewModel
import com.futo.circles.feature.room.select_users.SelectUsersViewModel
import com.futo.circles.feature.room.update_room.UpdateRoomViewModel
import com.futo.circles.feature.sign_up.SignUpViewModel
import com.futo.circles.feature.sign_up.setup_circles.SetupCirclesViewModel
import com.futo.circles.feature.sign_up.setup_profile.SetupProfileViewModel
import com.futo.circles.feature.sign_up.sign_up_type.SelectSignUpTypeViewModel
import com.futo.circles.feature.sign_up.terms.AcceptTermsViewModel
import com.futo.circles.feature.sign_up.validate_email.ValidateEmailViewModel
import com.futo.circles.feature.sign_up.validate_token.ValidateTokenViewModel
import com.futo.circles.feature.timeline.TimelineViewModel
import com.futo.circles.feature.timeline.post.CreatePostViewModel
import com.futo.circles.feature.timeline.post.emoji.EmojiViewModel
import com.futo.circles.feature.timeline.post.report.ReportViewModel
import com.futo.circles.model.CircleRoomTypeArg
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { LogInViewModel(get()) }
    viewModel { GroupsViewModel(get()) }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        TimelineViewModel(get { parametersOf(roomId, type) })
    }
    viewModel { (roomId: String) -> InviteMembersViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        ManageMembersViewModel(get { parametersOf(roomId, type) })
    }
    viewModel { (roomId: String?) -> SelectUsersViewModel(get { parametersOf(roomId) }) }
    viewModel { CreateRoomViewModel(get()) }
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
    viewModel { (roomId: String) -> UpdateRoomViewModel(get { parametersOf(roomId) }) }
    viewModel { CreatePostViewModel() }
    viewModel { (roomId: String, eventId: String) ->
        ReportViewModel(get { parametersOf(roomId, eventId) })
    }
    viewModel { EmojiViewModel(get()) }
    viewModel { (roomId: String) -> FollowingViewModel(get { parametersOf(roomId) }) }
}