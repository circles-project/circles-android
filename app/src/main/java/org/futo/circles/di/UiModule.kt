package org.futo.circles.di

import org.futo.circles.feature.bottom_navigation.SystemNoticesCountSharedViewModel
import org.futo.circles.feature.circles.CirclesViewModel
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteViewModel
import org.futo.circles.feature.circles.following.FollowingViewModel
import org.futo.circles.feature.groups.GroupsViewModel
import org.futo.circles.feature.log_in.LogInViewModel
import org.futo.circles.feature.log_in.stages.LoginStagesViewModel
import org.futo.circles.feature.log_in.stages.password.DirectLoginPasswordDataSource
import org.futo.circles.feature.log_in.stages.password.LoginBsSpekeDataSource
import org.futo.circles.feature.log_in.stages.password.LoginPasswordDataSource
import org.futo.circles.feature.log_in.stages.terms.LoginAcceptTermsDataSource
import org.futo.circles.feature.notices.SystemNoticesTimelineViewModel
import org.futo.circles.feature.people.PeopleViewModel
import org.futo.circles.feature.people.user.UserViewModel
import org.futo.circles.feature.photos.PhotosViewModel
import org.futo.circles.feature.photos.gallery.GalleryViewModel
import org.futo.circles.feature.photos.preview.MediaPreviewViewModel
import org.futo.circles.feature.photos.save.SavePostToGalleryViewModel
import org.futo.circles.feature.photos.select.SelectGalleriesViewModel
import org.futo.circles.feature.reauth.ReAuthStageViewModel
import org.futo.circles.feature.room.create_room.CreateRoomViewModel
import org.futo.circles.feature.room.invite.InviteMembersViewModel
import org.futo.circles.feature.room.manage_members.ManageMembersViewModel
import org.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelViewModel
import org.futo.circles.feature.room.select.SelectRoomsViewModel
import org.futo.circles.feature.room.select_users.SelectUsersViewModel
import org.futo.circles.feature.room.update_room.UpdateRoomViewModel
import org.futo.circles.feature.settings.SettingsViewModel
import org.futo.circles.feature.settings.active_sessions.ActiveSessionsViewModel
import org.futo.circles.feature.settings.active_sessions.verify.VerifySessionViewModel
import org.futo.circles.feature.settings.change_password.ChangePasswordViewModel
import org.futo.circles.feature.settings.edit_profile.EditProfileViewModel
import org.futo.circles.feature.share.BaseShareViewModel
import org.futo.circles.feature.sign_up.SignUpViewModel
import org.futo.circles.feature.sign_up.password.PasswordViewModel
import org.futo.circles.feature.sign_up.password.SignupBsSpekeDataSource
import org.futo.circles.feature.sign_up.password.SignupPasswordDataSource
import org.futo.circles.feature.sign_up.setup_circles.SetupCirclesViewModel
import org.futo.circles.feature.sign_up.setup_profile.SetupProfileViewModel
import org.futo.circles.feature.sign_up.sign_up_type.SelectSignUpTypeViewModel
import org.futo.circles.feature.sign_up.subscription_stage.SubscriptionStageViewModel
import org.futo.circles.feature.sign_up.terms.AcceptTermsViewModel
import org.futo.circles.feature.sign_up.terms.SignupAcceptTermsDataSource
import org.futo.circles.feature.sign_up.username.UsernameViewModel
import org.futo.circles.feature.sign_up.validate_email.ValidateEmailViewModel
import org.futo.circles.feature.sign_up.validate_token.ValidateTokenViewModel
import org.futo.circles.feature.timeline.TimelineViewModel
import org.futo.circles.feature.timeline.poll.CreatePollViewModel
import org.futo.circles.feature.timeline.post.create.CreatePostViewModel
import org.futo.circles.feature.timeline.post.report.ReportViewModel
import org.futo.circles.model.CircleRoomTypeArg
import org.futo.circles.model.PasswordModeArg
import org.futo.circles.model.TermsModeArg
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.parameter.parametersOf
import org.koin.dsl.module

val uiModule = module {
    viewModel { LogInViewModel(get()) }
    viewModel { GroupsViewModel(get()) }
    viewModel { CirclesViewModel(get()) }
    viewModel { PeopleViewModel(get(), get()) }
    viewModel { PhotosViewModel(get()) }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        TimelineViewModel(
            get { parametersOf(roomId, type) }, get { parametersOf(roomId) }, get(), get(), get()
        )
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
    viewModel { (mode: TermsModeArg) ->
        AcceptTermsViewModel(
            when (mode) {
                TermsModeArg.Login -> get<LoginAcceptTermsDataSource> { parametersOf(false) }
                TermsModeArg.Signup -> get<SignupAcceptTermsDataSource>()
                TermsModeArg.ReAuth -> get<LoginAcceptTermsDataSource> { parametersOf(true) }
            }
        )
    }
    viewModel { ValidateEmailViewModel(get()) }
    viewModel { SetupProfileViewModel(get()) }
    viewModel { SetupCirclesViewModel(get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { (levelValue: Int, myUserLevelValue: Int) ->
        ChangeAccessLevelViewModel(get { parametersOf(levelValue, myUserLevelValue) })
    }
    viewModel { (roomId: String) -> UpdateRoomViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String, eventId: String) ->
        ReportViewModel(get { parametersOf(roomId, eventId) })
    }
    viewModel { (roomId: String) -> FollowingViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> AcceptCircleInviteViewModel(get { parametersOf(roomId) }) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { ChangePasswordViewModel(get()) }
    viewModel { ActiveSessionsViewModel(get()) }
    viewModel { (roomId: String, type: CircleRoomTypeArg, isVideoAvailable: Boolean) ->
        GalleryViewModel(
            roomId,
            isVideoAvailable,
            get { parametersOf(roomId, type) },
            get { parametersOf(roomId) },
            get()
        )
    }
    viewModel { (roomId: String, eventId: String) ->
        MediaPreviewViewModel(roomId, eventId, get { parametersOf(roomId, eventId) }, get())
    }
    viewModel { (roomId: String, eventId: String) ->
        SavePostToGalleryViewModel(get { parametersOf(roomId, eventId) }, get())
    }
    viewModel { SelectGalleriesViewModel(get()) }
    viewModel { (userId: String) ->
        UserViewModel(get { parametersOf(userId) })
    }
    viewModel { SystemNoticesCountSharedViewModel() }
    viewModel { (roomId: String, type: CircleRoomTypeArg) ->
        SystemNoticesTimelineViewModel(get { parametersOf(roomId, type) })
    }
    viewModel { SubscriptionStageViewModel(get()) }
    viewModel { BaseShareViewModel(get()) }
    viewModel { (type: CircleRoomTypeArg) -> SelectRoomsViewModel(get { parametersOf(type) }) }
    viewModel { (passwordMode: PasswordModeArg) ->
        PasswordViewModel(
            when (passwordMode) {
                PasswordModeArg.LoginPasswordStage -> get<LoginPasswordDataSource> {
                    parametersOf(false)
                }
                PasswordModeArg.ReAuthPassword -> get<LoginPasswordDataSource> { parametersOf(true) }
                PasswordModeArg.LoginBsSpekeStage -> get<LoginBsSpekeDataSource> {
                    parametersOf(false, false)
                }
                PasswordModeArg.ReAuthBsSpekeLogin -> get<LoginBsSpekeDataSource> {
                    parametersOf(true, false)
                }
                PasswordModeArg.ReAuthBsSpekeSignup -> get<LoginBsSpekeDataSource> {
                    parametersOf(true, true)
                }
                PasswordModeArg.LoginDirect -> get<DirectLoginPasswordDataSource>()
                PasswordModeArg.SignupPasswordStage -> get<SignupPasswordDataSource>()
                PasswordModeArg.SignupBsSpekeStage -> get<SignupBsSpekeDataSource>()
            }
        )
    }
    viewModel { LoginStagesViewModel(get()) }
    viewModel { ReAuthStageViewModel(get()) }
    viewModel { UsernameViewModel(get()) }
    viewModel { (deviceId: String) -> VerifySessionViewModel(deviceId, get()) }
    viewModel { (roomId: String, eventId: String?, isEdit: Boolean) ->
        CreatePostViewModel(roomId, eventId, isEdit)
    }
    viewModel { (roomId: String, eventId: String?) -> CreatePollViewModel(roomId, eventId) }
}