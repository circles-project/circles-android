package org.futo.circles.di

import org.futo.circles.core.picker.device.PickDeviceMediaViewModel
import org.futo.circles.feature.bottom_navigation.SystemNoticesCountSharedViewModel
import org.futo.circles.feature.circles.CirclesViewModel
import org.futo.circles.feature.circles.accept_invite.AcceptCircleInviteViewModel
import org.futo.circles.feature.circles.following.FollowingViewModel
import org.futo.circles.feature.groups.GroupsViewModel
import org.futo.circles.feature.log_in.LogInViewModel
import org.futo.circles.feature.log_in.stages.password.LoginPasswordViewModel
import org.futo.circles.feature.notices.SystemNoticesTimelineViewModel
import org.futo.circles.feature.people.PeopleViewModel
import org.futo.circles.feature.people.user.UserViewModel
import org.futo.circles.feature.photos.PhotosViewModel
import org.futo.circles.feature.photos.gallery.GalleryViewModel
import org.futo.circles.feature.photos.preview.MediaPreviewViewModel
import org.futo.circles.feature.photos.save.SavePostToGalleryViewModel
import org.futo.circles.feature.photos.select.SelectGalleriesViewModel
import org.futo.circles.feature.room.create_room.CreateRoomViewModel
import org.futo.circles.feature.room.invite.InviteMembersViewModel
import org.futo.circles.feature.room.manage_members.ManageMembersViewModel
import org.futo.circles.feature.room.manage_members.change_role.ChangeAccessLevelViewModel
import org.futo.circles.feature.room.select.SelectRoomsViewModel
import org.futo.circles.feature.room.select_users.SelectUsersViewModel
import org.futo.circles.feature.room.update_room.UpdateRoomViewModel
import org.futo.circles.feature.settings.SettingsViewModel
import org.futo.circles.feature.settings.active_sessions.ActiveSessionsViewModel
import org.futo.circles.feature.settings.change_password.ChangePasswordViewModel
import org.futo.circles.feature.settings.edit_profile.EditProfileViewModel
import org.futo.circles.feature.share.BaseShareViewModel
import org.futo.circles.feature.sign_up.SignUpViewModel
import org.futo.circles.feature.sign_up.setup_circles.SetupCirclesViewModel
import org.futo.circles.feature.sign_up.setup_profile.SetupProfileViewModel
import org.futo.circles.feature.sign_up.sign_up_type.SelectSignUpTypeViewModel
import org.futo.circles.feature.sign_up.subscription_stage.SubscriptionStageViewModel
import org.futo.circles.feature.sign_up.terms.AcceptTermsViewModel
import org.futo.circles.feature.sign_up.validate_email.ValidateEmailViewModel
import org.futo.circles.feature.sign_up.validate_token.ValidateTokenViewModel
import org.futo.circles.feature.timeline.TimelineViewModel
import org.futo.circles.feature.timeline.post.emoji.EmojiViewModel
import org.futo.circles.feature.timeline.post.report.ReportViewModel
import org.futo.circles.model.CircleRoomTypeArg
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
    viewModel { AcceptTermsViewModel(get()) }
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
    viewModel { EmojiViewModel(get()) }
    viewModel { (roomId: String) -> FollowingViewModel(get { parametersOf(roomId) }) }
    viewModel { (roomId: String) -> AcceptCircleInviteViewModel(get { parametersOf(roomId) }) }
    viewModel { EditProfileViewModel(get()) }
    viewModel { ChangePasswordViewModel(get(), get(), get()) }
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
    viewModel { (isVideoAvailable: Boolean) ->
        PickDeviceMediaViewModel(isVideoAvailable, get())
    }
    viewModel { SubscriptionStageViewModel(get()) }
    viewModel { BaseShareViewModel(get()) }
    viewModel { (type: CircleRoomTypeArg) -> SelectRoomsViewModel(get { parametersOf(type) }) }
    viewModel { LoginPasswordViewModel(get()) }
}